'use strict';

const HIDE = 0;
const ERROR = 1;
const SUCCESS = 2;

let state = HIDE;

function displaySuccessfulResult(message) {
    let resultElement = document.getElementById('result');
    let resultText = document.getElementById('resultText');
    resultText.textContent = message;
    if (state !== SUCCESS) {
        if (state === ERROR)
            resultElement.classList.replace('alert-danger', 'alert-success');
        else {
            resultElement.classList.add('alert-success');
            resultElement.classList.remove('text-hide');
        }
        state = SUCCESS;
    }
}

function displayErrorResult(message) {
    let resultElement = document.getElementById('result');
    let resultText = document.getElementById('resultText');
    resultText.textContent = message;
    if (state !== ERROR) {
        if (state === SUCCESS)
            resultElement.classList.replace('alert-success', 'alert-danger');
        else {
            resultElement.classList.add('alert-danger');
            resultElement.classList.remove('text-hide');
        }
        state = ERROR;
    }
}

function doSubmit() {
    document.settings.submitButton.textContent = '正在同步';
    document.settings.submitButton.disabled = true;
    let settingsData = new FormData();
    settingsData.append('username', document.settings.phoneNumber.value);
    settingsData.append('password', document.settings.password.value);
    settingsData.append('email', document.settings.email.value);
    settingsData.append('emailPassword', document.settings.emailPassword.value);
    fetch('/api/settings', {method: 'POST', body: settingsData})
        .then(response => {
            if (response.ok)
                return response.json();
            else {
                if (response.status === 404) {
                    displayErrorResult('无法连接更新服务器，请检查服务是否正常运行。');
                    return null;
                } else {
                    response.json().then(value => displayErrorResult(value.error + ': ' + value.message));
                }
            }
        })
        .catch(reason => displayErrorResult(reason))
        .then(result => {
            if (result != null) {
                if (result.result === 0) {
                    displaySuccessfulResult('同步成功');
                } else {
                    displayErrorResult(result.message);
                }
            }
        }).catch(reason => {
        displayErrorResult(reason);
    }).catch(reason => {
        displayErrorResult(reason);
    }).finally(() => {
        document.settings.submitButton.textContent = '开始同步';
        document.settings.submitButton.disabled = false;
    });
    return false;
}
