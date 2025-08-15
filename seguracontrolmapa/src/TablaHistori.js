/**
 * @author Wilson Fernando Sánchez Santana
 * @copyright Copyright (c) 2025 - 2026 Wilson Sánchez
 * @license 2025 - 2026
 */

import React, { useState, useEffect } from 'react';
import { collection, query, where, onSnapshot, orderBy, Timestamp } from "firebase/firestore";
import { db } from './firebaseConfig';
import './TablaHistorial.css'; // Asegúrate de tener estilos

const TablaHistorial = ({
  showHistoryOnMap,
  showHistoryTable,
  centerMapOnLocation,
  setSelectedHistoryPoints,
  selectedMovil,
  currentLocations,
  setSelectedMovil
}) => {
  const [historial, setHistorial] = useState([]);
  const [loading, setLoading] = useState(false); // Nuevo estado para manejar la carga
  const [error, setError] = useState(null); // Nuevo estado para manejar errores

  // El estado de los filtros se manejará directamente en los inputs para una mejor UX
  const [fechaInicio, setFechaInicio] = useState("");
  const [fechaFin, setFechaFin] = useState("");

  useEffect(() => {
    // Si la tabla no debe mostrarse o no hay un móvil seleccionado, no se hace nada
    if (!showHistoryTable || !selectedMovil) {
      setHistorial([]);
      setSelectedHistoryPoints([]);
      return;
    }

    setLoading(true); // Iniciar carga
    setError(null); // Limpiar errores previos

    const historialRef = collection(db, "conductores", selectedMovil, "historial_ubicaciones");

    // Construir la consulta dinámicamente con los filtros de fecha
    let q;
    try {
      if (fechaInicio && fechaFin) {
        // Convertir las fechas de string a objetos Date de JavaScript
        const inicioDate = new Date(fechaInicio);
        const finDate = new Date(fechaFin);
        // Ajustar la hora final para cubrir todo el día seleccionado
        finDate.setHours(23, 59, 59, 999);

        // Crear Timestamps de Firestore para la consulta
        const inicioTimestamp = Timestamp.fromDate(inicioDate);
        const finTimestamp = Timestamp.fromDate(finDate);

        q = query(
          historialRef,
          where("timestamp", ">=", inicioTimestamp),
          where("timestamp", "<=", finTimestamp),
          orderBy("timestamp", "asc")
        );
      } else {
        // Si no hay filtro de fecha, se hace una consulta simple ordenada
        q = query(historialRef, orderBy("timestamp", "asc"));
      }
    } catch (e) {
      console.error("Error al construir la consulta de Firebase:", e);
      setError("Hubo un problema al aplicar el filtro de fechas. Por favor, revisa los formatos.");
      setLoading(false);
      return;
    }

    // Suscribirse a los cambios en la consulta
    const unsubscribe = onSnapshot(
      q,
      (snapshot) => {
        const data = snapshot.docs.map((doc) => ({
          id: doc.id,
          ...doc.data(),
        }));
        
        // Actualizar el estado del historial
        setHistorial(data);

        // Preparar los puntos para el mapa
        const puntosMapa = data.map((h) => ({
          id: h.id,
          lat: h.latitud,
          lng: h.longitud,
          conductor: h.conductor,
          movil: h.movil,
          timestamp: h.timestamp,
        }));
        setSelectedHistoryPoints(puntosMapa);
        setLoading(false); // Finalizar carga

        
        if (showHistoryOnMap && puntosMapa.length > 0) {
          const ultimoPunto = puntosMapa[puntosMapa.length - 1];
          centerMapOnLocation(ultimoPunto.lat, ultimoPunto.lng, 17);
        }
      },
      (err) => {
        console.error("Error al obtener historial:", err);
        setError("No se pudo cargar el historial. Verifica la conexión o el ID del conductor.");
        setHistorial([]);
        setLoading(false);
      }
    );

   
    return () => unsubscribe();
  }, [showHistoryTable, selectedMovil, fechaInicio, fechaFin, showHistoryOnMap, setSelectedHistoryPoints, centerMapOnLocation]);

  return (
    <div className="TablaHistorialContainer">
      <h2>Historial de Ubicaciones</h2>
      <div className="FiltrosContainer">
        <select onChange={(e) => setSelectedMovil(e.target.value)} value={selectedMovil || ""}>
          <option value="" disabled>Selecciona un móvil</option>
          {currentLocations.map(loc => (
            <option key={loc.movil} value={loc.movil}>{`Móvil ${loc.movil} - ${loc.conductor}`}</option>
          ))}
        </select>
        <input
          type="date"
          value={fechaInicio}
          onChange={(e) => setFechaInicio(e.target.value)}
        />
        <input
          type="date"
          value={fechaFin}
          onChange={(e) => setFechaFin(e.target.value)}
        />
        {/* El botón de filtrar ya no es necesario, el useEffect se activa con los cambios */}
      </div>

      <div className="TableWrapper">
        {loading && <p>Cargando historial...</p>}
        {error && <p className="ErrorText">{error}</p>}
        {!loading && !error && historial.length > 0 ? (
          <table>
            <thead>
              <tr>
                <th>Conductor</th>
                <th>Móvil</th>
                <th>Latitud</th>
                <th>Longitud</th>
                <th>Fecha y Hora</th>
              </tr>
            </thead>
            <tbody>
              {historial.map((h) => (
                <tr
                  key={h.id}
                  onClick={() => centerMapOnLocation(h.latitud, h.longitud, 17)}
                  style={{ cursor: "pointer" }}
                >
                  <td>{h.conductor}</td>
                  <td>{h.movil}</td>
                  <td>{h.latitud?.toFixed(5)}</td>
                  <td>{h.longitud?.toFixed(5)}</td>
                  <td>
                    {h.timestamp
                      ? new Date(h.timestamp.seconds * 1000).toLocaleString(
                          "es-EC",
                          { timeZone: "America/Guayaquil" }
                        )
                      : "N/A"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (!loading && !error) && (
          <p>No hay historial disponible para el rango de fechas seleccionado.</p>
        )}
      </div>
    </div>
  );
};

export default TablaHistorial;