import React, { useEffect, useState } from "react";
import axios from "axios";
import "../css/customizeProfile.css";

const MAX_BIO_LENGTH = 500;
const MAX_IMAGE_BYTES = 2 * 1024 * 1024;

export default function CustomizeProfile() {
  const [userEmail, setUserEmail] = useState(null); // fetched from session
  const [bio, setBio] = useState("");
  const [theme, setTheme] = useState("light");
  const [picture, setPicture] = useState(null);
  const [message, setMessage] = useState("");

  // 1️⃣ Get logged-in user on mount
  useEffect(() => {
    axios
      .get("http://localhost:8080/api/users/me", { withCredentials: true })
      .then((res) => {
        const user = res.data;
        setUserEmail(user.email);

        // Fetch health info for this user
        axios
          .get(`http://localhost:8080/api/health-info?email=${user.email}`, { withCredentials: true })
          .then((res) => {
            const info = res.data;
            setBio(info.bio || "");
            setTheme(info.theme || "light");
            setPicture(info.picture || null);
          })
          .catch((err) => {
            console.error("Failed to load health info:", err);
            setMessage("Failed to load profile.");
          });
      })
      .catch((err) => {
        console.error("Failed to get logged-in user:", err);
        setMessage("User not logged in.");
      });
  }, []);

  // 2️⃣ Apply theme to root
  useEffect(() => {
    const root = document.getElementById("root");
    root.classList.remove("theme-light", "theme-dark", "theme-colorful");
    root.classList.add(`theme-${theme}`);
  }, [theme]);

  // 3️⃣ Handle picture upload
  const handleImageChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (file.size > MAX_IMAGE_BYTES) {
      setMessage("Image too large (max 2MB).");
      return;
    }

    const reader = new FileReader();
    reader.onload = () => setPicture(reader.result);
    reader.readAsDataURL(file);
  };

  // 4️⃣ Save profile
  const handleSave = () => {
    if (!userEmail) {
      setMessage("Cannot save: user not logged in");
      return;
    }

    axios
      .put(`http://localhost:8080/api/health-info/profile/${userEmail}`, {
        bio,
        theme,
        picture,
      }, { withCredentials: true })
      .then(() => setMessage("Profile saved successfully."))
      .catch((err) => {
        console.error("Failed to save profile:", err);
        setMessage("Failed to save profile");
      });
  };

  return (
    <div className="customize-profile">
      <h2>Customize Profile</h2>

      <section className="profile-picture-section">
        <label>Profile Picture</label>
        <div className="picture-preview">
          {picture ? (
            <img src={picture} alt="Profile" />
          ) : (
            <div className="empty-avatar">No picture</div>
          )}
        </div>
        <input type="file" accept="image/*" onChange={handleImageChange} />
      </section>

      <section className="bio-section">
        <label>Bio</label>
        <textarea
          value={bio}
          onChange={(e) => setBio(e.target.value)}
          maxLength={MAX_BIO_LENGTH}
        />
        <div className="bio-meta">{bio.length}/{MAX_BIO_LENGTH}</div>
      </section>

      <section className="theme-section">
        <label>Theme</label>
        <div className="theme-options">
          {["light", "dark", "colorful"].map((t) => (
            <label key={t} className={`theme-option ${theme === t ? "active" : ""}`}>
              <input
                type="radio"
                name="theme"
                value={t}
                checked={theme === t}
                onChange={() => setTheme(t)}
              />
              {t.charAt(0).toUpperCase() + t.slice(1)}
            </label>
          ))}
        </div>
      </section>

      <button className="save" onClick={handleSave}>Save</button>

      {message && <div className="message">{message}</div>}
    </div>
  );
}
