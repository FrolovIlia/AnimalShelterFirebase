const admin = require("firebase-admin");
const { onDocumentCreated } = require("firebase-functions/v2/firestore");

admin.initializeApp();

exports.sendNewAnimalNotification = onDocumentCreated("animals/{animalId}", async (event) => {
  const newAnimal = event.data.data();

  const message = {
    notification: {
      title: `Добавлен питомец: ${newAnimal.name}`,
      body: `${newAnimal.age}, ${newAnimal.feature}`,
    },
    data: {
      animalKey: event.params.animalId,
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
