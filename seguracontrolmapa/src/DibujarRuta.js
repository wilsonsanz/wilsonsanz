/**
 * @author Wilson Fernando Sánchez Santana
 * @copyright Copyright (c) 2025 - 2026 Wilson Sánchez
 * @license 2025 - 2026
 */



import React, { useState, useCallback, useRef, useEffect } from 'react'; // Añadido useEffect
import { MapContainer, TileLayer, FeatureGroup, useMap, Polyline } from 'react-leaflet';
import { EditControl } from 'react-leaflet-draw';
import 'leaflet/dist/leaflet.css';
import 'leaflet-draw/dist/leaflet.draw.css';
import L from 'leaflet';
import './DibujarRuta.css';

// Corrige los iconos predeterminados de Leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png',
});

const MapReloader = () => {
  const map = useMap();
  React.useEffect(() => {
    map.invalidateSize();
  }, [map]);
  return null;
};

// ACEPTAMOS LA PROP 'onSaveRoute'
const DibujarRuta = ({ onSaveRoute }) => {
  const [drawnLayers, setDrawnLayers] = useState([]);
  const featureGroupRef = useRef();

  const defaultCenter = {
    lat: -2.1962,
    lng: -79.8862
  };

  // Asegúrate de que el FeatureGroup tenga una referencia válida
  // Este useEffect es crucial para que EditControl sepa a qué FeatureGroup adjuntarse
  useEffect(() => {
    if (featureGroupRef.current) {
      // Opcional: Podrías añadir lógica aquí si necesitas hacer algo con el grupo
    }
  }, []); // Se ejecuta una vez al montar

  const onCreated = useCallback((e) => {
    const { layerType, layer } = e;
    if (layerType === 'polyline') {
      const newLayer = layer;
      
      // Limpia explícitamente las polilíneas anteriores del FeatureGroup
      // antes de añadir la nueva. Esto asegura que solo haya una ruta activa para guardar.
      if (featureGroupRef.current) {
        featureGroupRef.current.clearLayers();
      }
      
      // Añade la nueva capa al FeatureGroup
      featureGroupRef.current.addLayer(newLayer);
      
      // Guarda la nueva capa en el estado para un control fácil
      setDrawnLayers([newLayer]);

      const latlngs = newLayer.getLatLngs().map(latlng => ({
        lat: latlng.lat,
        lng: latlng.lng
      }));
      console.log("Ruta dibujada (coordenadas):", latlngs);
    }
  }, []);

  const onEdited = useCallback((e) => {
    const { layers } = e;
    layers.eachLayer(layer => {
      if (layer instanceof L.Polyline) {
        const latlngs = layer.getLatLngs().map(latlng => ({
          lat: latlng.lat,
          lng: latlng.lng
        }));
        console.log("Ruta editada (coordenadas):", latlngs);
      }
    });
    // No necesitas actualizar drawnLayers aquí si solo manejas una polilínea
  }, []);

  const onDeleted = useCallback((e) => {
    // Si se borra una capa a través de la interfaz de edición, actualiza el estado
    const { layers } = e;
    layers.eachLayer(layer => {
      setDrawnLayers(prev => prev.filter(l => l._leaflet_id !== layer._leaflet_id));
      console.log("Capa borrada a través de la herramienta de edición.");
    });
  }, []);

  const clearAllDrawnRoutes = useCallback(() => {
    if (featureGroupRef.current) {
      featureGroupRef.current.clearLayers(); // <--- Esta es la línea clave para borrar del mapa
      setDrawnLayers([]); // <--- Esta es la línea clave para limpiar el estado
      console.log("Todas las rutas borradas.");
    }
  }, []); // No necesita dependencias porque solo usa ref y setter

  const getCurrentPolylineCoords = useCallback(() => {
    // Si solo permitimos una polilínea a la vez y la guardamos en drawnLayers[0]
    if (drawnLayers.length > 0 && drawnLayers[0] instanceof L.Polyline) {
      const polyline = drawnLayers[0];
      return polyline.getLatLngs().map(latlng => ({
        lat: latlng.lat,
        lng: latlng.lng
      }));
    }
    return null;
  }, [drawnLayers]);


  return (
    <div className="DrawingRouteContainer">
      <div className="DrawingMapContainer">
        <MapContainer
          center={defaultCenter}
          zoom={12}
          scrollWheelZoom={true}
          style={{ width: '100%', height: '100%' }}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <MapReloader />

          {/* El FeatureGroup es el contenedor donde react-leaflet-draw añade las capas */}
          <FeatureGroup ref={featureGroupRef}>
            <EditControl
              position="topright"
              onCreated={onCreated}
              onEdited={onEdited}
              onDeleted={onDeleted}
              draw={{
                rectangle: false,
                circle: false,
                marker: false,
                circlemarker: false,
                polygon: false,
                polyline: {
                  shapeOptions: {
                    color: '#FF0000',
                    weight: 4,
                    opacity: 0.8,
                  },
                },
              }}
              edit={{
                // Es crucial pasar la referencia del FeatureGroup aquí para que las herramientas
                // de edición/eliminación sepan qué capas controlar.
                featureGroup: featureGroupRef.current, // <--- Importante: asegúrate de que sea featureGroupRef.current
                edit: true,
                remove: true,
              }}
            />
          </FeatureGroup>
        </MapContainer>
      </div>
      <div className="DrawingControls">
        {/* Botón Borrar Rutas */}
        <button onClick={clearAllDrawnRoutes} className="ClearRouteButton">Borrar Todas las Rutas</button>
        {/* Botón Guardar Ruta */}
        <button onClick={() => {
          const currentPath = getCurrentPolylineCoords();
          if (currentPath && currentPath.length > 0) {
            onSaveRoute(currentPath);
            clearAllDrawnRoutes(); // Opcional: Borrar la ruta del mapa de dibujo después de guardarla
          } else {
            alert("No hay ruta dibujada para guardar.");
          }
        }} className="SaveRouteButton">Guardar Ruta</button>
      </div>
    </div>
  );
};

export default DibujarRuta;