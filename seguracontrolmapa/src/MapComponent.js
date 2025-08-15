/**
 * @author Wilson Fernando Sánchez Santana
 * @copyright Copyright (c) 2025 - 2026 Wilson Sánchez
 * @license 2025 - 2026
 */
import React, { useState, useEffect, useCallback } from 'react';
import { MapContainer, TileLayer, Marker, Polyline, useMap } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import defaultIcon from 'leaflet/dist/images/marker-icon.png';
import defaultIcon2x from 'leaflet/dist/images/marker-icon-2x.png';
import defaultRetina from 'leaflet/dist/images/marker-shadow.png';
import { collection, onSnapshot } from "firebase/firestore";
import { db } from './firebaseConfig';
import './MapComponent.css';
import TablaHistorial from './TablaHistori';

// Configuración de los iconos de Leaflet 
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: defaultIcon2x,
  iconUrl: defaultIcon,
  shadowUrl: defaultRetina,
});

const selectedPointIcon = new L.Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
  shadowUrl: defaultRetina,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

// Componente para actualizar el centro del mapa
const MapCenterUpdater = ({ lat, lng, zoom }) => {
  const map = useMap();
  useEffect(() => {
    if (map && lat && lng) {
      map.setView([lat, lng], zoom);
    }
  }, [map, lat, lng, zoom]);
  return null;
};
const MapResizer = () => {
  const map = useMap();
  useEffect(() => {
    // Retraso para asegurar que el contenedor del mapa está completamente renderizado
    const timer = setTimeout(() => {
      map.invalidateSize();
    }, 500); // Espera 500ms antes de redimensionar

    return () => clearTimeout(timer);
  }, [map]);
  return null;
};
const MapComponent = ({ savedRoutes, showSavedRoutesOnMap }) => {
  const [currentLocations, setCurrentLocations] = useState([]);
  const [showCurrentLocationsTable, setShowCurrentLocationsTable] = useState(true);
  const [showHistoryTable, setShowHistoryTable] = useState(false);
  const [showHistoryOnMap, setShowHistoryOnMap] = useState(false);
  const [selectedHistoryPoints, setSelectedHistoryPoints] = useState([]);
  const [selectedMovil, setSelectedMovil] = useState(null); 
  const [mapStyle, setMapStyle] = useState('osm');
  // 💡 Estado para controlar la posición de los botones
  const [isControlsInCorner, setIsControlsInCorner] = useState(false);

  const defaultCenter = { lat: -2.1962, lng: -79.8862 };
  const [mapCenter, setMapCenter] = useState(defaultCenter);
  const [mapZoom, setMapZoom] = useState(15);

  const centerMapOnLocation = useCallback((lat, lng, zoom = 17) => {
    if (!lat || !lng) return;
    setMapCenter({ lat, lng });
    setMapZoom(zoom);
  }, []);

  useEffect(() => {
    // Escucha en tiempo real para las ubicaciones actuales
    const unsubscribeCurrent = onSnapshot(
      collection(db, "ubicaciones_actuales"),
      (snapshot) => {
        const newLocations = [];
        snapshot.forEach((doc) => {
          newLocations.push({
            id: doc.id,
            ...doc.data()
          });
        });
        setCurrentLocations(newLocations);
      },
      (error) => {
        console.error("Error al escuchar ubicaciones actuales:", error);
      }
    );
    return () => unsubscribeCurrent();
  }, []); // Se ejecuta solo al montar el componente

  // 💡 useEffect para centrar el mapa al cargar las ubicaciones actuales
  useEffect(() => {
    if (!showHistoryOnMap && !showSavedRoutesOnMap && selectedHistoryPoints.length === 0) {
      if (currentLocations.length > 0) {
        // Encuentra el móvil seleccionado si existe
        const targetLocation = selectedMovil 
          ? currentLocations.find(loc => loc.movil === selectedMovil)
          : currentLocations[currentLocations.length - 1];
        
        if (targetLocation) {
          centerMapOnLocation(targetLocation.latitud, targetLocation.longitud, 15);
        }
      } else {
        centerMapOnLocation(defaultCenter.lat, defaultCenter.lng, 15);
      }
    }
  }, [
    currentLocations, 
    showHistoryOnMap, 
    showSavedRoutesOnMap, 
    selectedHistoryPoints, 
    selectedMovil, 
    centerMapOnLocation, 
    defaultCenter
  ]);
  const toggleMapStyle = () => {
    setMapStyle(prevStyle => (prevStyle === 'osm' ? 'satellite' : 'osm'));
  };

  const getTileLayer = () => {
    if (mapStyle === 'satellite') {
      return (
        <TileLayer
          attribution='&copy; <a href="https://www.esri.com/">Esri</a>, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, swisstopo, and the GIS User Community'
          url="https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"
        />
      );
    }
    return (
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
    );
  };
  
  // 💡 Función para manejar el clic en el botón de la tabla
  const handleToggleCurrentTable = () => {
    const newShowState = !showCurrentLocationsTable;
    setShowCurrentLocationsTable(newShowState);
    setShowHistoryTable(false);
    setShowHistoryOnMap(false);
    setSelectedHistoryPoints([]);
    setSelectedMovil(null);
    // Mueve los controles a la esquina si la tabla se oculta
    setIsControlsInCorner(!newShowState);
  };

  const handleToggleHistoryTable = () => {
    setShowHistoryTable(true);
    setShowCurrentLocationsTable(false);
    setShowHistoryOnMap(true);
    // Mueve los controles a la esquina
    setIsControlsInCorner(true);
  };

  return (
    <div className="DashboardContainer">
      {/* Contenedor del mapa */}
      <div className="MapContainer" style={{ flex: (showCurrentLocationsTable || showHistoryTable) ? 2 : 1 }}>
        <MapContainer
          center={mapCenter}
          zoom={mapZoom}
          scrollWheelZoom={true}
          style={{ height: '100%', width: '100%' }}
        >
          {getTileLayer()}
          <MapCenterUpdater lat={mapCenter.lat} lng={mapCenter.lng} zoom={mapZoom} />
          <MapResizer />
          
          {/* Marcadores de ubicaciones actuales (solo si no se muestra el historial) */}
          {!showHistoryOnMap && currentLocations.map((loc) => (
            <Marker
              key={loc.id}
              position={[loc.latitud, loc.longitud]}
              title={`Conductor: ${loc.conductor} (${loc.movil})`}
            />
          ))}
          
          {showHistoryOnMap && selectedHistoryPoints.length > 1 && (
            <Polyline
              positions={selectedHistoryPoints.map(loc => [loc.lat, loc.lng])}
              pathOptions={{
                color: 'blue',
                weight: 4,
                opacity: 0.8,
              }}
            />
          )}
          {showHistoryOnMap && selectedHistoryPoints.map((loc, index) => (
            <Marker
              key={`selected-history-${loc.id || index}`}
              position={[loc.lat, loc.lng]}
              icon={selectedPointIcon}
              title={`Historial: ${loc.conductor} (${loc.movil}) - ${new Date(loc.timestamp.seconds * 1000).toLocaleString('es-EC', { timeZone: 'America/Guayaquil' })}`}
            />
          ))}
          {showSavedRoutesOnMap && savedRoutes && savedRoutes.map((route) => (
            <Polyline
              key={route.id}
              positions={route.coords.map(coord => [coord.lat, coord.lng])}
              pathOptions={{
                color: route.color || '#007bff',
                weight: route.weight || 5,
                opacity: 0.7,
                dashArray: '10, 10',
              }}
            />
          ))}
        </MapContainer>
      </div>

      {/* Contenedor de tablas */}
      {(showCurrentLocationsTable || showHistoryTable) && (
        <div className="TableContainer">
          {showCurrentLocationsTable && (
            <>
              <div className="TableControls">
                <h2>Ubicaciones de Vehículos Actuales</h2>
              </div>
              {currentLocations.length > 0 ? (
                <div className="TableWrapper">
                  <table>
                    <thead>
                      <tr>
                        <th>Conductor</th>
                        <th>Móvil</th>
                        <th>Latitud</th>
                        <th>Longitud</th>
                        <th>Marca de Tiempo</th>
                      </tr>
                    </thead>
                    <tbody>
                      {currentLocations.map((loc) => (
                        <tr
                          key={loc.id}
                          onClick={() => {
                            centerMapOnLocation(loc.latitud, loc.longitud, 17);
                            setSelectedMovil(loc.movil); 
                          }}
                          style={{ cursor: 'pointer' }}
                        >
                          <td>{loc.conductor}</td>
                          <td>{loc.movil}</td>
                          <td>{loc.latitud?.toFixed(5)}</td>
                          <td>{loc.longitud?.toFixed(5)}</td>
                          <td>
                            {loc.timestamp
                              ? new Date(loc.timestamp.seconds * 1000).toLocaleString('es-EC', { timeZone: 'America/Guayaquil' })
                              : 'N/A'}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <p>No hay ubicaciones disponibles.</p>
              )}
            </>
          )}
          {showHistoryTable && (
            <TablaHistorial
              showHistoryTable={showHistoryTable}
              showHistoryOnMap={showHistoryOnMap}
              centerMapOnLocation={centerMapOnLocation}
              setSelectedHistoryPoints={setSelectedHistoryPoints}
              selectedMovil={selectedMovil} 
              currentLocations={currentLocations} 
              setSelectedMovil={setSelectedMovil} 
            />
          )}
        </div>
      )}

      {/* Botones */}
      <div className={`MapControls ${isControlsInCorner ? 'MapControls--corner' : ''}`}>
      <button
        className="ToggleTableButton"
        onClick={handleToggleCurrentTable}
      >
        {showCurrentLocationsTable ? 'Ocultar Tabla Actual' : 'Mostrar Ubicaciones Actuales'}
      </button>

      <button
        className="ToggleMapHistoryButton"
        onClick={handleToggleHistoryTable}
      >
        Mostrar Historial de Vehículos
      </button>

      <button
        className="ToggleMapStyleButton"
        onClick={toggleMapStyle}
        
      >
        {mapStyle === 'osm' ? 'Vista Satelital' : 'Vista de Mapa'}
      </button>
      </div>
    </div>
  );
};

export default MapComponent;
