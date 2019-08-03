const form = document.getElementById('form');

function handleSubmit(e) {
    e.preventDefault();

    const email = document.getElementById('email');
    const pwd = document.getElementById('pwd');
    const message = document.getElementById('message');

    const params = 'email=' + encodeURIComponent(email.value) + '&pwd=' + encodeURIComponent(pwd.value);

    fetch('/users/register', { method: 'POST', body: params, headers: { 'Content-Type': 'application/x-www-form-urlencoded' } })
        .then(response => response.json())
        .then(json => {
            if (json.errMessage !== '') {
                message.innerText = json.errMessage;
                return;
            }

            sessionStorage.setItem('token', json.token);
            location.href = '/';
        });
}

form.onsubmit = handleSubmit;