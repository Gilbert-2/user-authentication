document.addEventListener('DOMContentLoaded', function () {
  const signupButton = document.querySelector('button');

  signupButton.addEventListener('click', function (e) {
      const emailInput = document.querySelector('input[type="email"]');
      const passwordInput = document.querySelector('input[type="password"]');
      const confirmPasswordInput = document.querySelector('input[name="passwordcon"]');
      const usernameInput = document.querySelector('input[name="username"]');
      
      
      if (!emailInput.reportValidity() || !passwordInput.reportValidity() || !confirmPasswordInput.reportValidity() || !usernameInput.reportValidity()) {
          e.preventDefault();
          return;
      }

     
      const usernameValid = /^[A-Za-z]+$/.test(usernameInput.value);
      if (!usernameValid) {
          showError(usernameInput, "Username should contain only letters.");
          e.preventDefault();
          return;
      }

     
      if (passwordInput.value !== confirmPasswordInput.value) {
          showError(confirmPasswordInput, "Passwords do not match.");
          e.preventDefault();
          return;
      }

      if (passwordInput.value.length < 6) {
          showError(passwordInput, "Password should be at least 6 characters long.");
          e.preventDefault();
          return;
      }

      
      signupButton.disabled = true;
  });

  
  function showError(input, message) {
      const errorElement = document.createElement('div');
      errorElement.classList.add('error-message');
      errorElement.textContent = message;
      input.insertAdjacentElement('afterend', errorElement);

    
      input.addEventListener('input', function () {
          if (errorElement) {
              errorElement.remove();
          }
      });
  }
});
