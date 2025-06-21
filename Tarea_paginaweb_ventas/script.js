document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    form.addEventListener('submit', function(event) {
        event.preventDefault();
        alert('Gracias por su mensaje. Nos pondremos en contacto con usted pronto.');
        form.reset();
    });
});