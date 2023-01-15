const radios = document.querySelectorAll('input[name="protocol"]');

let clientId = "A";
const serverUrl = 'http://localhost:5000';
const wsUrl = "ws://localhost:5000";

var protocol = "poll";

var MSG_CHECK_INTERVAL = 1000;

var pollDaemonId = null;

var allMessages = [];

var webSocket = new WebSocket(wsUrl);

function initiateMessageService() {
    if (protocol === "poll"){
        pollDaemonId = setInterval(chkMsgPoll, MSG_CHECK_INTERVAL);
    }else{
        if (pollDaemonId != null){
            clearInterval(pollDaemonId);
            pollDaemonId = null;
        }
        if (protocol === "longpoll"){
            chkMsgLongPoll();
        }else{
            console.log("Starting WebSocket message service");
        }
    }
}

function chkMsgPoll(){
    console.log("Checking for new messages by poll protocol");
    const xhr = new XMLHttpRequest();
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

function chkMsgLongPoll(){
    console.log("Checking for new messages by long poll protocol");
    const xhr = new XMLHttpRequest();
    xhr.open('POST', serverUrl + '/message/get');

    xhr.onload = function() {
        if (this.status === 200) {
            console.log("Found a new message");
            addAndDisplayNewRecievedMessage(JSON.parse(this.responseText).message);
            chkMsgLongPoll();
        }else{
            // ako nije vratio 200OK, znači da se ovaj protokol više ne koristi tako da nemoj ponovo pozvati chkMsgLongPoll
            return;
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

    document.getElementById("message").value = "";
    addAndDisplayNewSentMessage(message);
}

function sendMessagePoll(message){
    const xhr = new XMLHttpRequest();
    xhr.open("POST", serverUrl + "/message/send", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({ from: clientId, protocol: protocol, message: message }));
}

function sendMessageLongPoll(message){
    const xhr = new XMLHttpRequest();
    xhr.open("POST", serverUrl + "/message/send", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({ from: clientId, protocol: protocol, message: message }));
}

function sendMessageWebSocket(message){
}

function registerClient(selectedProtocol, user){
    protocol = document.querySelector('input[name="protocol"]:checked').value;
    clientId = document.querySelector('input[name="user"]:checked').value;
    console.log("Registering client at server as client " + clientId);
    if (protocol === "poll" || protocol === "longpoll"){
        const xhr = new XMLHttpRequest();
        xhr.open("POST", serverUrl + "/register", true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onload = function() {
            if (this.status === 200){
                console.log("Successfully registered!");
                console.log(this.response);
                initiateMessageService();
            }else{
                console.log("Error while attempting to register client!");
            }
        }
        xhr.send(JSON.stringify({ from: clientId, protocol: protocol }));
    }else{
        initiateMessageService();
    }


}

webSocket.onopen = function(){
    console.log("WebSocket opened. Sending register message");
    webSocket.send(JSON.stringify({from: clientId, message: "register"}))
}

webSocket.onmessage = function(msgEvent){
    console.log(msgEvent.data);
}



function addAndDisplayNewSentMessage(message){
    let myMsg;

    if (clientId === "A"){
        myMsg = "A";
    }else{
        myMsg = "B";
    }

    allMessages.push("[" + myMsg + "]:" + message);
    displayMessages();
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
