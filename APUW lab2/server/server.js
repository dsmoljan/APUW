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

    res.end('Registration Successful!');

    if (req.body.from === 'A' && clientAProtocol !== "longpoll"){
        closeLongPoll("A");
    }

    if (req.body.from === 'B' && clientBProtocol !== "longpoll"){
        closeLongPoll("B");
    }
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


app.get('/status', (req, res) => {
    res.status(200);
    res.end("OK");
})

// kod websocketa možda neću koristiti ovaj endpoint, nego svaki put u metodi /message/send, kad primim poruku od B za A, onda ću pozvati metodu proslijediPorukuWebSocket, koja će preko otvorenog websocketa
// proslijediti tu poruku prema A
// a otvaranje web socketa npr. napravim u register metodi

app.post('/message/get', (req, res) => {
    console.log("Received a new message get request")
    console.log(req.body);

    if (req.body.from === 'A'){
        if (req.body.protocol === "poll"){
            handlePollMsgRequest(req, res);
        }else if (req.body.protocol === "longpoll"){
            clientALongPollRes = res;
            if (messagesForA.length !== 0){
                handleLongPollMsgResponse(res, messagesForA.pop());
                clientALongPollRes = null;
            }
        }
    }else {
        if (req.body.protocol === "poll"){
            handlePollMsgRequest(req, res);
        }else if (req.body.protocol === "longpoll"){
            clientBLongPollRes = res;
            if (messagesForB.length !== 0){
                handleLongPollMsgResponse(res, messagesForB.pop());
                clientBLongPollRes = null;
            }
        }
    }
})

function handlePollMsgRequest(req, res) {
    let body = null;

    if (req.body.from === 'A') {
        if (messagesForA.length === 0){
            res.status(204).end();
        }else{
            res.setHeader("Content-Type", "application/json");
            body = JSON.stringify({message: messagesForA.pop()});
            res.status(200).end(body);
        }
    } else {
        if (messagesForB.length === 0){
            res.status(204).end();
        }else{
            res.setHeader("Content-Type", "application/json");
            body = JSON.stringify({message: messagesForB.pop()});
            res.status(200).end(body);
        }
    }
}

function handleLongPollMsgResponse(res, message){
    let body = null;
    res.setHeader("Content-Type", "application/json");
    body = JSON.stringify({message: message});
    res.status(200).end(body);
}

function closeLongPoll(client) {
    if (client === "A"){
        if (clientALongPollRes !== null){
            clientALongPollRes.status(204).end();
            clientALongPollRes = null;
            console.log("Closing long poll with client A");
        }
    }

    if (client === "B"){
        if (clientBLongPollRes !== null){
            clientBLongPollRes.status(204).end();
            clientBLongPollRes = null;
            console.log("Closing long poll with client B");
        }
    }
}

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})