const admin = require("firebase-admin");
const { onDocumentCreated } = require("firebase-functions/v2/firestore");

admin.initializeApp();

exports.sendNewAnimalNotification = onDocumentCreated("animals/{animalId}", async (event) => {
  const newAnimal = event.data;

  const message = {
    notification: {
      title: "Новый питомец!",
      body: `Появился ${newAnimal.name}`,
    },
    topic: "new_animals",
  };

  try {
    const response = await admin.messaging().send(message);
    console.log("Уведомление отправлено:", response);
  } catch (error) {
    console.error("Ошибка отправки уведомления:", error);
  }
});
