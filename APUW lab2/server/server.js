const express = require('express')
var cors = require('cors')
const app = express()

const WebSocket = require('ws');

const port = 5000;

var messagesForA = [];
var messagesForB = [];

var clientAProtocol = null;
var clientBProtocol = null;

var clientALongPollRes = null;
var clientBLongPollRes = null;

var clientAWebSocket = null;
var clientBWebSocket = null;

app.use(cors());
app.use(express.json());

const server = app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})

const wss = new WebSocket.Server({ server });


// ovo samo kaze "kad dobijes get zahtjev na /, sto napraviti
app.post('/register', (req, res) => {
    console.log("Received a new registration request")
    console.log(req.body);

    if (req.body.from === 'A'){
        clientAProtocol = req.body.protocol;
    }else{
        clientBProtocol = req.body.protocol;
    }

    if (req.body.from === 'A'){
        closeLongPoll("A");
    }

    if (req.body.from === 'B'){
        closeLongPoll("B");
    }

    res.set('Access-Control-Allow-Origin', '*');
    res.send('Registration Successful!');
})

// ovo je za slucaj xmlHttpRequesta
// ali hej sad mi je palo na pamet što brani serveru da sluša na sve moguće načine hmm
app.post('/message/send', (req, res) => {
    console.log("Received a new message")
    console.log(req.body);

    if (req.body.from === 'A'){
        messagesForB.push(req.body.message);
        if (clientBProtocol === "longpoll"){
            handleLongPollMsgResponse(clientBLongPollRes, messagesForB.pop());
        }else if (clientBProtocol === "websocket"){
            handleWebSocketMessageResponse(clientBWebSocket, messagesForB.pop());
        }
    }else{
        messagesForA.push(req.body.message);
        if (clientAProtocol === "longpoll"){
            handleLongPollMsgResponse(clientALongPollRes, messagesForA.pop());
        }else if (clientAProtocol === "websocket") {
            handleWebSocketMessageResponse(clientAWebSocket, messagesForA.pop());
        }
    }

    // falio ti je ovaj res.end lol
    res.end();
    console.log(messagesForB);
})


// kod websocketa možda neću koristiti ovaj endpoint, nego svaki put u metodi /message/send, kad primim poruku od B za A, onda ću pozvati metodu proslijediPorukuWebSocket, koja će preko otvorenog websocketa
// proslijediti tu poruku prema A
// a otvaranje web socketa npr. napravim u register metodi

app.post('/message/get', (req, res) => {
    console.log("Received a new message get request")
    console.log(req.body);

    if (req.body.from === 'A'){
        if (clientAProtocol === "poll"){
            handlePollMsgRequest(req, res);
        }else if (clientAProtocol === "longpoll"){
            clientALongPollRes = res;
            if (messagesForA.length !== 0){
                handleLongPollMsgResponse(res, messagesForA.pop());
            }
        }else{
            handleWebSocketMessageResponse()
        }
    }else {
        if (clientBProtocol === "poll"){
            handlePollMsgRequest(req, res);
        }else if (clientBProtocol === "longpoll"){
            clientBLongPollRes = res;
            if (messagesForB.length !== 0){
                handleLongPollMsgResponse(res, messagesForB.pop());
            }
        }
    }
})

function handlePollMsgRequest(req, res) {
    let body = null;

    if (req.body.from === 'A') {
        if (messagesForA.length === 0){
            res.status(204);
            res.send();
        }else{
            res.status(200);
            body = JSON.stringify({message: messagesForA.pop()});
            res.setHeader("Content-Type", "application/json");
            res.send(body);
        }
    } else {
        if (messagesForB.length === 0){
            res.status(204);
            res.send();
        }else{
            res.status(200);
            body = JSON.stringify({message: messagesForB.pop()});
            res.send(body);
        }
    }
}

function handleLongPollMsgResponse(res, message){
    let body = null;
    res.status(200);
    body = JSON.stringify({message: message});
    res.setHeader("Content-Type", "application/json");
    res.send(body);
}

function closeLongPoll(client) {
    if (client === "A"){
        if (clientALongPollRes !== null){
            clientALongPollRes.status(204);
            clientALongPollRes.send();
            clientALongPollRes = null;
        }
    }

    if (client === "B"){
        if (clientBLongPollRes !== null){
            clientBLongPollRes.status(204);
            clientBLongPollRes.send();
            clientBLongPollRes = null;
        }
    }
}

wss.on('connection', (ws) => {
    //connection is up, let's add a simple simple event
    ws.on('message', (message) => {
        //log the received message and send it back to the client
        console.log('Received WebSocket message: %s', message);

        const messageJson = JSON.parse(message);

        if (messageJson.from === 'A'){
            if (messageJson.message === "register"){
                clientAWebSocket = ws;
                clientAProtocol = "websocket";
                if (messagesForA.length !== 0){
                    handleWebSocketMessageResponse(clientAWebSocket, messagesForA.pop());
                }            }else{
                messagesForB.push(messageJson.message);
                if (clientBProtocol === "longpoll"){
                    handleLongPollMsgResponse(clientBLongPollRes, messagesForB.pop());
                }else if (clientBProtocol === "websocket"){
                    handleWebSocketMessageResponse(clientBWebSocket, messagesForB.pop());
                }
            }
        }else{
            if (messageJson.message === "register"){
                clientBWebSocket = ws;
                clientBProtocol = "websocket";
                if (messagesForB.length !== 0){
                    handleWebSocketMessageResponse(clientBWebSocket, messagesForB.pop());
                }
            }else{
                messagesForA.push(messageJson.message);
                if (clientAProtocol === "longpoll"){
                    handleLongPollMsgResponse(clientALongPollRes, messagesForA.pop());
                }else if (clientAProtocol === "websocket"){
                    handleWebSocketMessageResponse(clientAWebSocket, messagesForA.pop());
                }
            }
        }
    });

    ws.on('close', (message) => {
        if (clientAWebSocket === ws){
            console.log("WebSocket for A closed");
            clientAWebSocket = null;
        }else if (clientBWebSocket === ws){
            console.log("WebSocket for B closed");
            clientBWebSocket = null;
        }else{
            console.log("Unknown WebSocket closed! Possible error!");
        }
    })
});

function handleWebSocketMessageResponse(clientWebSocket, message) {
    if (clientWebSocket !== null) {
        clientWebSocket.send(JSON.stringify({message: message}));
    }
}
