const radios = document.querySelectorAll('input[name="protocol"]');

const clientId = "A";
const serverUrl = 'http://localhost:5000';

var protocol = "poll";

for (let i = 0; i < radios.length; i++) {
    radios[i].addEventListener('change', function() {
        if (this.checked) {
            selectedOptionChanged(this.value);
        }
    });
}

function selectedOptionChanged(option) {
    console.log(`Selected option changed to ${option}`);
    protocol = option;
}


function sendMessage(message) {
    console.log(`Sending message ${message}`)
}

window.addEventListener('load', function() {
    registerClient();
});

function registerClient(){
    console.log("Registering client at server");
    const xhr = new XMLHttpRequest();
    xhr.open("POST", serverUrl + "/register", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onload = function() {
        if (this.status == 200){
            console.log("Successfully registered!");
            console.log(this.response);
        }
    }
    xhr.send(JSON.stringify({ from: clientId, message }));

}