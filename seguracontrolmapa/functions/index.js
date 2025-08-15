// /functions/index.js

const functions = require("firebase-functions");
const admin = require("firebase-admin");

// Inicializa el SDK de Admin
admin.initializeApp();

exports.sendNotificationByTopic = functions.https.onCall(async (data, context) => {
  const topic = data.topic;
  const messageText = data.message;

  if (!topic || !messageText) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "El tema y el mensaje son requeridos."
    );
  }

  const payload = {
    notification: {
      title: "Notificación de Seguimiento",
      body: messageText,
    },
    topic: topic, // Usamos 'topic' en lugar de 'token'
  };

  try {
    const response = await admin.messaging().send(payload);
    console.log("Notificación FCM enviada con éxito al tema:", topic, "Respuesta:", response);
    return { success: true, messageId: response };
  } catch (error) {
    console.error("Fallo al enviar notificación FCM al tema:", topic, "Error:", error);
    throw new functions.https.HttpsError(
      "unknown",
      "Fallo al enviar notificación al tema.",
      error.message
    );
  }
});