const express = require('express')
var cors = require('cors')
const app = express()

const port = 5000;

var messagesForA = [];
var messagesForB = [];

var clientAProtocol = "none";
var clientBProtocol = "none";

var clientALongPollRes = null;
var clientBLongPollRes = null;

app.use(cors());
app.use(express.json());


// ovo samo kaze "kad dobijes get zahtjev na /, sto napraviti
app.post('/register', (req, res) => {
    console.log("Received a new registration request")
    console.log(req.body);

    if (req.body.from === 'A'){
        clientAProtocol = req.body.protocol;
    }else{
        clientBProtocol = req.body.protocol;
    }

    if (req.body.from === 'A' && clientAProtocol !== "longpoll"){
        closeLongPoll("A");
    }

    if (req.body.from === 'B' && clientBProtocol !== "longpoll"){
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
        }
    }else{
        messagesForA.push(req.body.message);
        if (clientAProtocol === "longpoll"){
            handleLongPollMsgResponse(clientALongPollRes, messagesForA.pop());
        }
    }

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





app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})