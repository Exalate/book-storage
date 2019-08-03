let shelfId = null;
let bookId = null;
let pageNumber = null;
const token = sessionStorage.getItem('token');

if (!token) {
    location.href = '/login.html';
} else {
    getBookshelves();
}

const uploadForm = document.getElementById('uploadForm');
uploadForm.onsubmit = handleUpload;

const createShelfForm = document.getElementById('createShelfForm');
createShelfForm.onsubmit = handleCreateShelf;

// Зарузка книги
function handleUpload(e) {
    e.preventDefault();

    const formData = new FormData(uploadForm);
    formData.append('bookshelf_id', shelfId);

    fetch('/books/upload', {
        method: 'POST',
        body: formData,
        headers: { 'x-access-token': sessionStorage.getItem('token') }
    })
        .then(response => response.json())
        .then(json => {
            if (json.errMessage !== '') {
                const uploadFormLog = document.getElementById('uploadFormLog');
                uploadFormLog.innerText = json.errMessage;
            } else {
                const ul = document.getElementById('books');

                for (let key in json.books) {
                    const book = json.books[key];

                    const li = document.createElement('li');
                    const a = document.createElement('a');
                    a.href = '#';
                    a.innerText = book;
                    a.onclick = function(e) {
                        e.preventDefault();
                        handleBookClick(key);
                    }
                    li.appendChild(a);
                    ul.appendChild(li);
                }
            }
        });
}

// Получение списка полок
function getBookshelves() {
    fetch('/bookshelves', {
        headers: { 'x-access-token': sessionStorage.getItem('token') }
    })
        .then(response => response.json())
        .then(json => {
            const ul = document.getElementById('bookshelves');

            for (let key in json.bookshelves) {
                const shelf = json.bookshelves[key];

                const li = document.createElement('li');
                const a = document.createElement('a');
                a.href = '#';
                a.innerText = shelf;
                a.onclick = function(e) {
                    e.preventDefault();
                    handleBookshelfClick(key, shelf, this);
                }
                li.appendChild(a);
                ul.appendChild(li);
            }
        })
}

// Обработка клика по полке
function handleBookshelfClick(id, name, node) {
    shelfId = id;

    fetch(`/books?bookshelfId=${id}`, {
        headers: { 'x-access-token': sessionStorage.getItem('token') }
    })
        .then(response => response.json())
        .then(json => {
            const uploadBtn = document.getElementById('uploadBtn');
            uploadBtn.disabled = false;

            const booksTitle = document.getElementById('booksTitle');
            booksTitle.innerText = `Список книг на полке "${name}"`;

            const ul = document.getElementById('books');
            ul.innerHTML = '';

            for (const key in json.books) {
                const book = json.books[key];

                const li = document.createElement('li');
                const a = document.createElement('a');
                a.href = '#';
                a.innerText = book;
                a.onclick = function(e) {
                    e.preventDefault();
                    handleBookClick(key);
                }
                li.appendChild(a);
                ul.appendChild(li);
            }
        })
}

// Создание книжной полки
function handleCreateShelf(e) {
    e.preventDefault();
    const shelfName = document.getElementById('shelfName');

    fetch('/bookshelves', {
        body:  'name=' + encodeURIComponent(shelfName.value),
        method: 'POST',
        headers: {
            'x-access-token': sessionStorage.getItem('token'),
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
        .then(response => response.json())
        .then(json => {
            const ul = document.getElementById('bookshelves');

            for (const key in json.bookshelves) {
                const shelf = json.bookshelves[key];

                const li = document.createElement('li');
                const a = document.createElement('a');
                a.href = '#';
                a.innerText = shelf;
                a.onclick = function(e) {
                    e.preventDefault();
                    handleBookshelfClick(key, shelf, this);
                }
                li.appendChild(a);
                ul.appendChild(li);
            }
        })
}

// Обработка клика по книге
function handleBookClick(id) {
    bookId = id;

     fetch('/books/' + id, {
        headers: {
            'x-access-token': sessionStorage.getItem('token'),
        }
    })
        .then(response => response.json())
        .then(json => {
            const text = document.getElementById('text');
            text.innerText = json.pageText;

            const currentPage = document.getElementById('currentPage');
            currentPage.innerText = pageNumber = json.pageNumber;
        })
}

const prevPage = document.getElementById('prevPage');
const nextPage = document.getElementById('nextPage');

prevPage.onclick = function() {
    if (pageNumber === 1 || !bookId) return;

    fetch(`/books/${bookId}/page/${pageNumber - 1}`, {
        headers: {
            'x-access-token': sessionStorage.getItem('token'),
        }
    })
        .then(response => response.json())
        .then(json => {
            if (json.errMessage !== '') {
                return;
            }

            const text = document.getElementById('text');
            text.innerText = json.pageText;

            const currentPage = document.getElementById('currentPage');
            currentPage.innerText = pageNumber = json.pageNumber;
        })
}

nextPage.onclick = function() {
    if (!bookId) return;

    fetch(`/books/${bookId}/page/${pageNumber + 1}`, {
        headers: {
            'x-access-token': sessionStorage.getItem('token'),
        }
    })
        .then(response => response.json())
        .then(json => {
            if (json.errMessage !== '') {
                return;
            }

            const text = document.getElementById('text');
            text.innerText = json.pageText;

            const currentPage = document.getElementById('currentPage');
            currentPage.innerText = pageNumber = json.pageNumber;
        })
}

const addBookmark = document.getElementById('addBookmark');

addBookmark.onclick = function() {
    fetch(`/books/${bookId}/page/${pageNumber}/addBookmark`, {
        headers: {
            'x-access-token': sessionStorage.getItem('token'),
        }
    })
}