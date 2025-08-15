import React, { useState } from 'react';
import './Imagen.css';

function Imagen() {
  const [images, setImages] = useState([]);
  const [currentImage, setCurrentImage] = useState(null);
  const [newImageUrls, setNewImageUrls] = useState('');
  const [gridColumns, setGridColumns] = useState(4); // Estado para el número de columnas

  const handleThumbnailClick = (image) => {
    setCurrentImage(image);
  };

  const handleAddImages = (e) => {
    e.preventDefault();
    const urls = newImageUrls.split('\n').filter(url => url.trim() !== '');

    if (urls.length > 0) {
      const updatedImages = [...images, ...urls];
      setImages(updatedImages);
      if (!currentImage) {
        setCurrentImage(urls[0]);
      }
      setNewImageUrls('');
    }
  };

  return (
    <div className="image-viewer-container">
      {/* Formulario para subir múltiples URLs */}
      <form onSubmit={handleAddImages} className="add-images-form">
        <textarea
          value={newImageUrls}
          onChange={(e) => setNewImageUrls(e.target.value)}
          placeholder="Pega las URLs de las imágenes aquí, una por línea..."
          rows="5"
          className="image-textarea"
        />
        <button type="submit" className="add-images-button">
          Añadir Imágenes
        </button>
      </form>

      {/* Visor de la imagen principal */}
      <div className="main-image-wrapper">
        {currentImage ? (
          <img src={currentImage} alt="Imagen principal" className="main-image" />
        ) : (
          <div className="placeholder-text">Sube imágenes para empezar.</div>
        )}
      </div>

      {/* Controles para el grid */}
      <div className="grid-controls">
        <label>Miniaturas por fila:</label>
        <button 
          className={gridColumns === 2 ? 'active-grid-button' : ''}
          onClick={() => setGridColumns(2)}>
          2x2
        </button>
        <button 
          className={gridColumns === 3 ? 'active-grid-button' : ''}
          onClick={() => setGridColumns(3)}>
          3x3
        </button>
        <button 
          className={gridColumns === 4 ? 'active-grid-button' : ''}
          onClick={() => setGridColumns(4)}>
          4x4
        </button>
      </div>

      {/* Contenedor de miniaturas */}
      <div className="thumbnails-container" style={{ '--grid-cols': gridColumns }}>
        {images.map((image, index) => (
          <img
            key={index}
            src={image}
            alt={`Miniatura ${index + 1}`}
            className={`thumbnail ${currentImage === image ? 'active' : ''}`}
            onClick={() => handleThumbnailClick(image)}
          />
        ))}
      </div>
    </div>
  );
}

export default Imagen;