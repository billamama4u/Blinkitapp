const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendOrderNotification = functions.https.onRequest((req, res) => {
    const { title, body } = req.body;

    const message = {
        notification: {
            title: title,
            body: body,
        },
        topic: "orderNotifications",
    };

    admin.messaging().send(message)
        .then((response) => {
            console.log("Successfully sent message:", response);
            return res.status(200).send("Notification sent successfully");
        })
        .catch((error) => {
            console.log("Error sending message:", error);
            return res.status(500).send("Error sending notification");
        });
});
