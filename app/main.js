   Parse.Cloud.define("pushsample", (request) => {

       return Parse.Push.send({
           channels: ["Messages"],
           data: {
               title: "Hello from the Cloud Code",
               alert: "Back4App rocks!",
           }
       }, { useMasterKey: true });
   });