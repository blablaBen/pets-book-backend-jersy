var MongoClient = require('mongodb').MongoClient;

var dbConnection = null;

var lockCount = 0;

function getDbConnection(callback){
    MongoClient.connect("mongodb://localhost/app", function(err, db){
        if(err){
            console.log("Unable to connect to Mongodb");
        }else{
            console.log("Connected to Database!");
            db.dropDatabase();
            setTimeout(function() {
                db.close();
            }, 1000)
        }
    });
};

getDbConnection();