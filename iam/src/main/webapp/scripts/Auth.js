document.addEventListener("DOMContentLoaded", () => {
    // Sign-Up Functionality
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
                const response = await fetch("https://smartparkingcot.me/iam-1.0/rest-iam/identity/register", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ email, username, password }),
                });

                if (response.ok) {
                    const data = await response.json();
                    alert(data.message); // Show success message
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

    // Activation Functionality
    const activationForm = document.getElementById("activationForm");

    if (activationForm) {
        activationForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const activationCode = document.getElementById("activationCode").value.trim();

            try {
                const response = await fetch(`https://smartparkingcot.me/iam-1.0/rest-iam/identity/register/activate/${activationCode}`, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    alert(data.message); // Show success message
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

    // Sign-In Functionality
    const signInForm = document.getElementById("signInForm");

    if (signInForm) {
        signInForm.addEventListener("submit", async (event) => {
            event.preventDefault(); // Prevent default form submission behavior

            const username = document.getElementById("username").value.trim();
            const password = document.getElementById("password").value.trim();

            if (!username || !password) {
                displayError("usernameError", "Username is required.");
                displayError("passwordError", "Password is required.");
                return;
            }

            // Step 1: Generate state and code challenge
            const state = generateRandomString(20); // Dynamically generate state
            sessionStorage.setItem("oauth_state", state);

            const codeVerifier = generateRandomString(43); // Generate a secure code verifier
            const codeChallenge = await generateCodeChallenge(codeVerifier);

            try {
                // Step 2: Call the `/authorize` endpoint
                const authorizeResponse = await fetch(
                    `https://smartparkingcot.me/iam-1.0/rest-iam/authorize?state=${state}&code_challenge=${codeChallenge}`
                );
                if (!authorizeResponse.ok) throw new Error("Authorization failed.");

                // Step 3: Authenticate the user
                const authenticateResponse = await fetch("https://smartparkingcot.me/iam-1.0/rest-iam/authenticate", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                    },
                    body: new URLSearchParams({
                        username,
                        password,
                    }),
                });

                if (authenticateResponse.status === 302) {
                    const authenticateData = await authenticateResponse.json();
                    const returnedState = authenticateData.state;

                    // Validate state to prevent CSRF
                    if (returnedState !== sessionStorage.getItem("oauth_state")) {
                        throw new Error("Invalid state. Potential CSRF attack detected.");
                    }

                    const authorizationCode = authenticateData.AuthorizationCode;

                    // Step 4: Exchange the authorization code for an access token
                    const tokenResponse = await fetch(
                        `https://smartparkingcot.me/iam-1.0/rest-iam/oauth/token?authorization_code=${authorizationCode}&code_verifier=${codeVerifier}`
                    );
                    if (!tokenResponse.ok) throw new Error("Failed to exchange authorization code.");

                    const tokenData = await tokenResponse.json();
                    sessionStorage.setItem("access_token", tokenData.accessToken);

                    alert("Sign-in successful!");
                    window.location.href = "./Dashboard.html"; // Redirect user to dashboard
                } else {
                    throw new Error("Authentication failed. Please check your credentials.");
                }
            } catch (error) {
                console.error("Error during sign-in:", error);
                alert(error.message || "An error occurred during the authentication process.");
            }
        });
    }
});

// Utility functions
function displayError(elementId, message) {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.style.display = "block";
}

function generateRandomString(length) {
    const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    let result = "";
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    return result;
}

async function generateCodeChallenge(verifier) {
    const data = new TextEncoder().encode(verifier);
    const hash = await crypto.subtle.digest("SHA-256", data);
    return btoa(String.fromCharCode(...new Uint8Array(hash)))
        .replace(/=/g, "")
        .replace(/\+/g, "-")
        .replace(/\//g, "_");
}
