const express = require('express')
var cors = require('cors')
const app = express()

const port = 5000;

var messagesForA = ["poruka"];
var messagesForB = [];

var clientAProtocol = "none";
var clientBProtocol = "none";

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

    res.set('Access-Control-Allow-Origin', '*');
    res.send('Hello World!');
})

// ovo je za slucaj xmlHttpRequesta
// ali hej sad mi je palo na pamet što brani serveru da sluša na sve moguće načine hmm
app.post('/message/send', (req, res) => {
    console.log("Received a new message")
    console.log(req.body);

    if (req.body.from === 'A'){
        messagesForB.push(req.body.message);
    }else{
        messagesForA.push(req.body.message);
    }

    console.log(messagesForB);
})

app.post('/message/get', (req, res) => {
    console.log("Received a new message get request")
    console.log(req.body);
    let body = null;

    console.log(messagesForA);

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
})


app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})