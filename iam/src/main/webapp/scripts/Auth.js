document.addEventListener("DOMContentLoaded", () => {
    const signInForm = document.getElementById("signInForm");

    // Handle sign-in submission
    signInForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();

        // Clear any previous error messages
        const usernameError = document.getElementById("usernameError");
        const passwordError = document.getElementById("passwordError");
        usernameError.textContent = "";
        passwordError.textContent = "";

        try {
            // Send the POST request to authenticate
            const response = await fetch("http://localhost:8080/iam-1.0/rest-iam/authenticate", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`,
                credentials: "include", // Ensure cookies are sent with the request
            });

            if (response.ok) {
                const data = await response.json();
                console.log("Authentication successful. Response Data:", data);

                // Store the authorization code for future requests
                sessionStorage.setItem("authCode", data.AuthorizationCode);

                alert("Sign-In Successful! Redirecting to your dashboard...");
                window.location.href = "Dashboard.html"; // Redirect to Dashboard
            } else {
                const errorData = await response.json();
                console.error("Authentication failed. Error Response:", errorData);

                // Show error message to the user
                alert(errorData.message || "Invalid credentials. Please try again.");
            }
        } catch (error) {
            console.error("Error during sign-in:", error);
            alert("An error occurred. Please check your connection and try again.");
        }
    });
});



document.addEventListener("DOMContentLoaded", () => {
    const signUpForm = document.getElementById("signUpForm");

    if (signUpForm) {
        signUpForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const email = document.getElementById("email").value.trim();
            const username = document.getElementById("username").value.trim();
            const password = document.getElementById("password").value.trim();

            const emailError = document.getElementById("emailError");
            const usernameError = document.getElementById("usernameError");
            const passwordError = document.getElementById("passwordError");
            emailError.textContent = "";
            usernameError.textContent = "";
            passwordError.textContent = "";

            try {
                const response = await fetch("http://localhost:8080/iam-1.0/rest-iam/identity/register", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ email, username, password }),
                });

                if (response.ok) {
                    const data = await response.json();
                    alert(data.message);
                    window.location.href = "Activation.html"; // Redirect to Activation page
                } else {
                    const errorData = await response.json();
                    alert(errorData.error || "Registration failed.");
                }
            } catch (error) {
                console.error("Error during sign-up:", error);
                alert("An error occurred during registration.");
            }
        });
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const activationForm = document.getElementById("activationForm");

    if (activationForm) {
        activationForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const activationCode = document.getElementById("activationCode").value.trim();

            try {
                const response = await fetch(`http://localhost:8080/iam-1.0/rest-iam/identity/register/activate/${activationCode}`, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    alert(data.message);
                    window.location.href = "SignIn.html"; // Redirect to Sign-In page
                } else {
                    const errorData = await response.json();
                    alert(errorData.error || "Activation failed.");
                }
            } catch (error) {
                console.error("Error during activation:", error);
                alert("An error occurred during activation.");
            }
        });
    }
});
