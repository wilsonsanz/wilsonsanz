/**
 * @author Wilson Fernando Sánchez Santana
 * @copyright Copyright (c) 2025 - 2026 Wilson Sánchez
 * @license 2025 - 2026
 */
// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import { getFirestore } from "firebase/firestore";
import { initializeAppCheck, ReCaptchaV3Provider } from "firebase/app-check";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyA7DuudMCe-iq1_-a_72G1RWiWuvy8S7B4",
  authDomain: "segura-control-3d875.firebaseapp.com",
  projectId: "segura-control-3d875",
  storageBucket: "segura-control-3d875.firebasestorage.app",
  messagingSenderId: "729296790429",
  appId: "1:729296790429:web:3de8bb1e0e3a280c65c574",
  measurementId: "G-EZ3L8T2QD6"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const db = getFirestore(app);
// Inicializa App Check
// Reemplaza "TU_SITE_KEY" con la clave del sitio reCAPTCHA v3 que obtuviste en la consola de Firebase
initializeAppCheck(app, {
  provider: new ReCaptchaV3Provider('6LevZ48rAAAAAPpmR7ajSrDtK_U3zhdI9BNX3cSo'),
  isDebugging: false // **¡IMPORTANTE!** Deshabilita esto en producción
});

console.log("App Check inicializado.");

export { db, app, analytics };