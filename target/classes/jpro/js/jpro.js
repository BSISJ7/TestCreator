//function enableMicrophone() {
//    navigator.mediaDevices.getUserMedia({ audio: true })
//        .then(function(stream) {
//            // Here you can use the microphone stream
//            handleMicrophoneStream(stream);
//        })
//        .catch(function(err) {
//            // Handle the error
//            console.error(err);
//        });
//        console.log("Microphone has been enabled");
//}

function handleMicrophoneStream(stream) {
    // This is where you handle the microphone stream
    // For example, you can create an audio context and connect it to the stream
    var audioContext = new (window.AudioContext || window.webkitAudioContext)();
    var source = audioContext.createMediaStreamSource(stream);
    // Do something with the source...
    console.log("Microphone stream has been handled");

}

function getLocalStream() {
  navigator.mediaDevices
    .getUserMedia({ video: false, audio: true })
    .then((stream) => {
      window.localStream = stream; // A
      window.localAudio.srcObject = stream; // B
      window.localAudio.autoplay = true; // C
    })
    .catch((err) => {
      console.error(`you got an error: ${err}`);
    });
}


console.log("jpro.js file has been loaded");
