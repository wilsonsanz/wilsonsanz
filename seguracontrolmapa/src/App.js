/**
 * @author Wilson Fernando Sánchez Santana
 * @copyright Copyright (c) 2025 - 2026 Wilson Sánchez
 * @license 2025 - 2026
 */
import React, { useState, useEffect, useRef, useCallback } from 'react';
import { getAuth, signInWithEmailAndPassword } from 'firebase/auth';
import MapComponent from './MapComponent';
import NotificationsComponent from './NotificationsComponent';
import DibujarRuta from './DibujarRuta';
import './App.css';
import Imagen from './Imagenxxx.js';

// Componente MatrixBackground para el efecto de números cayendo
const MatrixBackground = () => {
  const canvasRef = useRef(null);

  // Configuración de los caracteres y el color del efecto Matrix
  const matrixChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890@#$%^&*()_+=-{}[]|:;"<>,.?/~`';
  const textColor = '#0F0';

  // Función para dibujar el efecto Matrix en el canvas
  const drawMatrix = useCallback(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    // Ajusta el tamaño del canvas para que coincida con el tamaño de la ventana
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    // Calcula el tamaño de la fuente y el número de columnas basado en el ancho del canvas
    const fontSize = 16;
    const columns = canvas.width / fontSize;
    const drops = [];

    for (let x = 0; x < columns; x++) {
      drops[x] = 1;
    }

    const animate = () => {
      ctx.fillStyle = 'rgba(0, 0, 0, 0.05)';
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      ctx.fillStyle = textColor;
      ctx.font = `${fontSize}px monospace`;

      for (let i = 0; i < drops.length; i++) {
        const text = matrixChars.charAt(Math.floor(Math.random() * matrixChars.length));
        ctx.fillText(text, i * fontSize, drops[i] * fontSize);

        if (drops[i] * fontSize > canvas.height && Math.random() > 0.97) {
          drops[i] = 0;
        }

        drops[i]++;
      }
      requestAnimationFrame(animate);
    };

    animate();
  }, []);

  useEffect(() => {
    drawMatrix();

    window.addEventListener('resize', drawMatrix);

    return () => {
      window.removeEventListener('resize', drawMatrix);
    };
  }, [drawMatrix]);

  return (
    <canvas ref={canvasRef} className="absolute inset-0 z-10"></canvas>
  );
};

// Componente LoginPage para manejar la autenticación
const LoginPage = ({ onLogin }) => {
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [email, setEmail] = useState('');
  const [seguraEpVisible, setSeguraEpVisible] = useState(false);
  const [seguraEpScale, setSeguraEpScale] = useState(0);
  const [seguraEpOpacity, setSeguraEpOpacity] = useState(0);
  const [flash, setFlash] = useState(false);

  const seguraEpRef = useRef(null);
  const auth = getAuth();
  useEffect(() => {
    setTimeout(() => {
      setSeguraEpVisible(true);
      const intervalId = setInterval(() => {
        setSeguraEpScale(prev => (prev < 1 ? prev + 0.1 : 1));
        setSeguraEpOpacity(prev => (prev < 1 ? prev + 0.15 : 1));
        if (seguraEpScale >= 1) clearInterval(intervalId);
      }, 50);

      setTimeout(() => {
        setFlash(true);
        setTimeout(() => setFlash(false), 300);
      }, 1000);
    }, 500);
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      await signInWithEmailAndPassword(auth, email, password);
      onLogin();
    } catch (firebaseError) {
      console.error(firebaseError.code, firebaseError.message);
      switch (firebaseError.code) {
        case 'auth/user-not-found':
          setError('No existe un usuario con ese correo.');
          break;
        case 'auth/wrong-password':
          setError('Contraseña incorrecta.');
          break;
        case 'auth/invalid-email':
          setError('El formato del correo electrónico es inválido.');
          break;
        default:
          setError('Error al iniciar sesión. Inténtalo de nuevo.');
          break;
      }
    }
  };

  return (
    <div className="relative flex items-center justify-center min-h-screen bg-gray-900 overflow-hidden">
      <MatrixBackground />
      {seguraEpVisible && (
        <div
          ref={seguraEpRef}
          className={`absolute top-1/4 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-[#00FF00] font-bold text-center text-5xl md:text-8xl transition-transform duration-300 transition-opacity duration-500 z-30 ${flash ? 'shadow-lime-500 shadow-lg' : ''}`}
          style={{
            transform: `translate(-50%, -50%) scale(${seguraEpScale})`,
            opacity: seguraEpOpacity,
            textShadow: '0 0 10px rgba(13, 250, 13, 0.8)',
          }}
        >
          SEGURA EP
        </div>
      )}
      <div className="relative z-20 bg-gray-800 p-6 rounded-lg shadow-xl shadow-lime-500/30 border border-lime-600 w-11/12 max-w-md">
        <h2 className="text-2xl md:text-3xl font-bold text-lime-400 mb-6 text-center">Inicio de Sesión</h2>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="email" className="block text-gray-300 text-sm font-bold mb-2">
              Correo Electrónico
            </label>
            <input
              type="email"
              id="email"
              className="shadow appearance-none border border-gray-700 rounded-lg w-full py-3 px-4 text-gray-200 leading-tight focus:outline-none focus:ring-2 focus:ring-lime-500 bg-gray-700 transition-all duration-300"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="admin@seguraep.com"
              required
            />
          </div>
          <div className="mb-4">
            <label htmlFor="password" className="block text-gray-300 text-sm font-bold mb-2">
              Contraseña
            </label>
            <input
              type="password"
              id="password"
              className="shadow appearance-none border border-gray-700 rounded-lg w-full py-3 px-4 text-gray-200 leading-tight focus:outline-none focus:ring-2 focus:ring-lime-500 bg-gray-700 transition-all duration-300"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Ingresa la contraseña"
              required
            />
          </div>
          {error && <p className="text-red-500 text-sm italic mb-4 text-center">{error}</p>}
          <div className="flex items-center justify-center">
            <button
              type="submit"
              className="bg-lime-600 hover:bg-lime-700 text-white font-bold py-3 px-6 rounded-lg focus:outline-none focus:shadow-outline transition-all duration-300 transform hover:scale-105 shadow-lg shadow-lime-500/30"
            >
              Iniciar Sesión
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [activeView, setActiveView] = useState('mapa');
  const [showNotificationMenu, setShowNotificationMenu] = useState(false);
  const [savedRoutes, setSavedRoutes] = useState([]);
  const [showSavedRoutesOnMap, setShowSavedRoutesOnMap] = useState(true);

  const handleLogin = () => {
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
  };

  const toggleNotificationMenu = () => {
    setShowNotificationMenu(prev => !prev);
  };

  const handleSaveRoute = (newRouteCoords) => {
    const newRoute = {
      id: Date.now(),
      coords: newRouteCoords,
      color: '#FF0000',
      weight: 4
    };
    setSavedRoutes(prevRoutes => [...prevRoutes, newRoute]);
    alert("¡Ruta guardada y lista para mostrar en el mapa!");
    setActiveView('mapa');
  };

  const handleDeleteAllSavedRoutes = () => {
    setSavedRoutes([]);
    alert("¡Todas las rutas guardadas han sido borradas!");
  };

  const toggleSavedRoutesVisibility = () => {
    setShowSavedRoutesOnMap(prev => !prev);
    setShowNotificationMenu(false);
  };


  if (!isLoggedIn) {
    return <LoginPage onLogin={handleLogin} />;
  }

  return (
    <div className="App min-h-screen flex flex-col font-sans bg-gray-900 text-white">
      <header className="bg-gradient-to-r from-gray-900 to-black text-[#00FF00] p-4 md:p-8 shadow-lg shadow-lime-500/30 rounded-b-xl border-b-2 border-lime-500 flex flex-col md:flex-row items-center justify-between relative">
        <button
          onClick={handleLogout}
          className="absolute top-4 left-4 px-3 py-1 bg-red-700 hover:bg-red-600 text-white rounded-lg transition-colors duration-300 shadow-md z-30"
        >
          Cerrar Sesión
        </button>

        <nav className="container mx-auto flex flex-col md:flex-row justify-between items-center font-mono space-y-4 md:space-y-0">
          <div className="flex flex-col items-center mb-4 md:mb-0">
        <h1 className="text-2xl md:text-4xl font-bold text-[#00FF00]">
       <span className="animate-pulse"></span>  SEGURA CONTROL
       </h1>
       <p className="text-xs text-cyan-200">
                 Hecho por Wilson Sánchez
                </p>
                </div>
          
          <div className="flex flex-col items-center space-y-2 md:space-y-0 md:flex-row md:space-x-4">
            <button
              onClick={() => setActiveView('mapa')}
              className={`text-sm md:text-xl px-3 py-1 md:px-5 md:py-2 rounded-lg transition-all duration-300 w-full md:w-auto
                ${activeView === 'mapa' ? 'bg-lime-600 text-white shadow-md' : 'hover:text-[#00FF00] hover:bg-lime-700/20'}`}
            >
              Mapa
            </button>
            <button
              onClick={() => setActiveView('ruta')}
              className={`text-sm md:text-xl px-3 py-1 md:px-5 md:py-2 rounded-lg transition-all duration-300 w-full md:w-auto
                ${activeView === 'ruta' ? 'bg-lime-600 text-white shadow-md' : 'hover:text-[#00FF00] hover:bg-lime-700/20'}`}
            >
              Dibujar Ruta
            </button>
            <button
              onClick={() => setActiveView('notificaciones')}
              className={`text-sm md:text-xl px-3 py-1 md:px-5 md:py-2 rounded-lg transition-all duration-300 w-full md:w-auto
                ${activeView === 'notificaciones' ? 'bg-lime-600 text-white shadow-md' : 'hover:text-[#00FF00] hover:bg-lime-700/20'}`}
            >
              Base de Datos
            </button>
           

            <div className="relative w-full md:w-auto">
              <button
                onClick={toggleNotificationMenu}
                className="text-sm md:text-xl px-3 py-1 md:px-4 md:py-2 rounded-lg bg-lime-700 hover:bg-lime-600 transition-colors duration-300 shadow-md w-full md:w-auto flex items-center justify-center space-x-2"
              >
                <span>Menú</span>
                <svg
                  className={`w-4 h-4 md:w-5 md:h-5 transition-transform duration-300 ${showNotificationMenu ? 'rotate-180' : 'rotate-0'}`}
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path>
                </svg>
              </button>

              {/* Menú desplegable */}
              {showNotificationMenu && (
                <div className="absolute top-full right-0 mt-2 w-48 bg-gray-800 border border-lime-600 rounded-lg shadow-xl z-[9999]">
                  {activeView === 'mapa' && (
                    <>
                      <button
                        onClick={toggleSavedRoutesVisibility}
                        className="block w-full text-left px-4 py-2 text-lime-300 hover:bg-lime-700 hover:text-white transition-colors duration-200"
                      >
                        {showSavedRoutesOnMap ? 'Ocultar Rutas Guardadas' : 'Mostrar Rutas Guardadas'}
                      </button>
                      <button
                        onClick={() => {
                          handleDeleteAllSavedRoutes();
                          setShowNotificationMenu(false);
                        }}
                        className="block w-full text-left px-4 py-2 text-lime-300 hover:bg-lime-700 hover:text-white rounded-b-lg transition-colors duration-200"
                      >
                        Borrar Todas las Rutas Guardadas
                      </button>
                    </>
                  )}
                </div>
              )}
            </div>
          </div>
        </nav>
      </header>

      {/* Contenido principal */}
      <main className="flex-grow bg-gray-900 text-white flex">
        {activeView === 'mapa' && (
          <MapComponent
            savedRoutes={savedRoutes}
            showSavedRoutesOnMap={showSavedRoutesOnMap}
          />
        )}
        {activeView === 'ruta' && <DibujarRuta onSaveRoute={handleSaveRoute} />}
        {activeView === 'notificaciones' && <NotificationsComponent />}
        {activeView === 'imagenes' && <Imagen />}
      </main>
    </div>
  );
}

export default App;
