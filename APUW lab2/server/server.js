const express = require('express')
var cors = require('cors')
const app = express()

const port = 5000

app.use(cors())
app.use(express.json())

// ovo samo kaze "kad dobijes get zahtjev na /, sto napraviti
app.post('/register', (req, res) => {
    console.log("Received a new registration request")
    console.log(req.body);
    res.set('Access-Control-Allow-Origin', '*');
    res.send('Hello World!');
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})