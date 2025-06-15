const functions = require('firebase-functions');
const admin = require('firebase-admin');
const { onDocumentCreated } = require('firebase-functions/v2/firestore');

admin.initializeApp();

exports.sendNewAnimalNotification = onDocumentCreated("animals/{animalId}", async (event) => {
    const newAnimal = event.data.data();
    const animalId = event.params.animalId;

    const animalName = newAnimal.name;
    const animalFeature = newAnimal.feature;

    if (!animalName || !animalFeature) {
        console.log('New animal has no name or feature, skipping notification.');
        return null;
    }

    const payload = {
        data: {
            title: 'Новое животное в приюте!',
            body: `Знакомьтесь: ${animalName}: ${animalFeature}`,
            animalId: animalId,
            animalName: animalName,
            animalFeature: animalFeature,
            screen: 'detailScreen'
        }
    };

    const topic = 'new_animals';
    try {
        await admin.messaging().sendToTopic(topic, payload);
        console.log('Successfully sent data-only message to topic:', topic);
    } catch (error) {
        console.error('Error sending message to topic:', topic, error);
    }

    return null;
});