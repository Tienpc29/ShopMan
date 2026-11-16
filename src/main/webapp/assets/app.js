(() => {
  document.querySelectorAll('.product-thumb img').forEach((image) => {
    const showPlaceholder = () => {
      image.hidden = true;
      image.nextElementSibling.hidden = false;
    };
    image.addEventListener('error', showPlaceholder);
    if (image.complete && image.naturalWidth === 0) showPlaceholder();
  });
})();
