/**
 * @author Wilson Fernando Sánchez Santana
 * @copyright Copyright (c) 2025 - 2026 Wilson Sánchez
 * @license 2025 - 2026
 */


import React, { useState, useEffect } from 'react';
import './NotificationsComponent.css';

const GoogleSheetEmbed = () => {
    // URL base de la hoja de cálculo para editar
    const baseUrl = "https://docs.google.com/spreadsheets/d/13UyqITFu8DGiLKWr4r9jW3Z3acDu8fesN5vkUa1Vl3s/edit";
    // Estado para la URL del iframe, inicializada con la primera hoja
    const [googleSheetUrl, setGoogleSheetUrl] = useState(
        `${baseUrl}?gid=1843360905&rm=minimal`
    );

    useEffect(() => {
        document.body.style.margin = '0';
        document.body.style.padding = '0';
        document.documentElement.style.height = '100%';
        document.body.style.height = '100%';

        return () => {
            document.body.style.margin = '';
            document.body.style.padding = '';
            document.documentElement.style.height = '';
            document.body.style.height = '';
        };
    }, []);
    
    // Función para cambiar de hoja
    const handleSheetChange = (gid) => {
        setGoogleSheetUrl(`${baseUrl}?gid=${gid}&rm=minimal`);
    };

    return (
        <div className="google-sheet-container">
            <div className="button-bar">
                <button
                    onClick={() => handleSheetChange('281690760')}
                    className="sheet-button"
                >
                    INGRESO
                </button>
                <button
                    onClick={() => handleSheetChange('1186319536')}
                    className="sheet-button"
                >
                    SALIDA
                </button>
                <button
                    onClick={() => handleSheetChange('0')}
                    className="sheet-button"
                >
                    NOVEDADES
                </button>
                <button
                    onClick={() => handleSheetChange('653316148')}
                    className="sheet-button"
                >
                    ENVIO DE RUTAS
                </button>
                <button
                    onClick={() => handleSheetChange('237005234')}
                    className="sheet-button"
                >
                    DATOS DE TIEMPOS PERSONALES
                </button>
                <button
                    onClick={() => handleSheetChange('523863154')}
                    className="sheet-button"
                >
                    DATOS COMBUSTIBLE
                </button>
                <button
                    onClick={() => handleSheetChange('1843360905')}
                    className="sheet-button"
                >
                    ADMINISTRADOR DE CREDENCIALES
                </button>
            </div>
            <iframe
                title="Google Sheet"
                src={googleSheetUrl}
                width="100%"
                height="100%"
                className="google-sheet-iframe"
                sandbox="allow-scripts allow-forms allow-same-origin"
            ></iframe>
        </div>
    );
};

export default GoogleSheetEmbed;