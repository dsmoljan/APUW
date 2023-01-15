const radios = document.querySelectorAll('input[name="protocol"]');

let clientId = "A";
const serverUrl = 'http://localhost:5000';

var protocol = "poll";

var MSG_CHECK_INTERVAL = 1000;

var pollDaemonId = null;

var allMessages = [];

// for (let i = 0; i < radios.length; i++) {
//     radios[i].addEventListener('change', function() {
//         if (this.checked) {
//             selectedOptionChanged(this.value);
//         }
//     });
// }
//
// function selectedOptionChanged(option) {
//     console.log(`Selected option changed to ${option}`);
//     protocol = option;
//     registerClient();
//     initiateMessageService();
// }

function initiateMessageService() {
    if (protocol === "poll"){
        pollDaemonId = setInterval(chkMsgPoll, MSG_CHECK_INTERVAL);
    }else{
        clearInterval(pollDaemonId);
    }

}

function chkMsgPoll(){
    console.log("Checking for new messages by poll protocol");
    var xhr = new XMLHttpRequest();
    xhr.open('POST', serverUrl + '/message/get');
    xhr.onload = function() {
        // a server bi mogao npr. vratiti 204 ako nema novih poruka
        if (this.status === 200) {
            console.log("Found a new message");
            addAndDisplayNewRecievedMessage(JSON.parse(this.responseText).message);
            //document.getElementById('messages').innerText = JSON.parse(this.responseText).message;
        }
    };

    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({ from: clientId, protocol: protocol }));

}



function sendMessage(message) {
    console.log(`Sending message ${message}`)
    if (protocol === "poll"){
        sendMessagePoll(message);
    }else if (protocol === "longpoll"){
        sendMessageLongPoll(message);
    }else{
        sendMessageWebSocket(message);
    }
}

function sendMessagePoll(message){
    const xhr = new XMLHttpRequest();
    xhr.open("POST", serverUrl + "/message/send", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({ from: clientId, protocol: protocol, message: message }));
}

function sendMessageLongPoll(message){
}

function sendMessageWebSocket(message){
}


// window.addEventListener('load', function() {
//     registerClient();
//     listenForMessages();
//     initiateMessageService();
// });

function registerClient(selectedProtocol, user){


    protocol = document.querySelector('input[name="protocol"]:checked').value;
    clientId = document.querySelector('input[name="user"]:checked').value;
    console.log("Registering client at server");
    const xhr = new XMLHttpRequest();
    xhr.open("POST", serverUrl + "/register", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onload = function() {
        if (this.status === 200){
            console.log("Successfully registered!");
            console.log(this.response);
        }
    }
    xhr.send(JSON.stringify({ from: clientId, protocol: protocol }));

    listenForMessages();
    initiateMessageService();
}

function listenForMessages() {
}

function addAndDisplayNewSentMessage(){

}

function addAndDisplayNewRecievedMessage(message){
    let msgFromUser;

    if (clientId === "A"){
        msgFromUser = "B";
    }else{
        msgFromUser = "A";
    }

    allMessages.push("[" + msgFromUser + "]:" + message);
    displayMessages();

}

function displayMessages(){
    let messagesDiv = document.getElementById("messages");
    messagesDiv.innerHTML = ""; // clear the messages div
    for (let i = 0; i < allMessages.length; i++){
        let messageNode = document.createElement("div");
        messageNode.innerHTML = allMessages[i];
        messagesDiv.appendChild(messageNode);
    }
}
