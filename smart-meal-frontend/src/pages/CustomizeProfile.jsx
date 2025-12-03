import React, { useEffect, useState } from "react";
import "../css/customizeProfile.css";

//const STORAGE_KEY = "userProfile";
const MAX_BIO_LENGTH = 500;
const MAX_IMAGE_BYTES = 2 * 1024 * 1024; // 2MB

function loadProfile(uid) {
  try {
    const raw = localStorage.getItem(`userProfile_${uid}`);
    return raw ? JSON.parse(raw) : { bio: "", theme: "light", picture: null };
  } catch (e) {
    console.warn("Failed to parse profile from storage", e);
    return { bio: "", theme: "light", picture: null };
  }
}

function saveProfile(uid, profile) {
  localStorage.setItem(`userProfile_${uid}`, JSON.stringify(profile));
}

const CustomizeProfile = ({ onThemeChange }) => {
  const user = JSON.parse(localStorage.getItem("user"))
  const uid = user && user.uid ? user.uid : null;
  
  const [bio, setBio] = useState("");
  const [theme, setTheme] = useState("");
  const [picture, setPicture] = useState(null); // base64 string
  const [message, setMessage] = useState("");
  const [showConfirm, setShowConfirm] = useState(false);
  const [showToast, setShowToast] = useState(false);


  useEffect(() => {
    if (!uid) return;
    const p = loadProfile(uid);
    setBio(p.bio);
    setTheme(p.theme);
    setPicture(p.picture);
  }, [uid]);

  useEffect(() => {
  if (onThemeChange) onThemeChange(theme);
  },  [theme, onThemeChange]);

  const handleImageChange = (e) => {
    const file = e.target.files && e.target.files[0];
    if (!file) return;

    if (file.size > MAX_IMAGE_BYTES) {
      setMessage("Image is too large (max 2MB). Choose a smaller file.");
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      setPicture(reader.result);
      setMessage("");
    };
    reader.onerror = () => setMessage("Failed to read image file.");
    reader.readAsDataURL(file);
  };

  const handleSave = () => {
    if (bio.length > MAX_BIO_LENGTH) {
      setMessage(`Bio is too long (max ${MAX_BIO_LENGTH} characters).`);
      return;
    }

    saveProfile(uid, { bio, theme, picture });
    setMessage("Profile saved.");
  };

  // open confirmation modal
  const handleReset = () => setShowConfirm(true);

  // perform reset after confirmation
  const confirmReset = () => {
    const empty = { bio: "", theme: "light", picture: null };
    setBio(empty.bio);
    setTheme(empty.theme);
    setPicture(empty.picture);
    saveProfile(uid, empty);
    setShowConfirm(false);
    setShowToast(true);
    // also set a small message accessible for screen readers
    setMessage("Profile reset to defaults.");
    // auto-dismiss toast after 3s
    setTimeout(() => setShowToast(false), 3000);
  };

  const cancelReset = () => setShowConfirm(false);

  return (
    <div className="customize-profile">
      {/* toast notification */}
      {showToast && (
        <div className="toast">Preferences were reset to defaults.</div>
      )}
      <h2>Customize Profile</h2>

      <section className="profile-picture-section">
        <label htmlFor="profilePicture">Profile picture</label>
        <div className="picture-preview">
          {picture ? (
            <img src={picture} alt="Profile" />
          ) : (
            <div className="empty-avatar">No picture</div>
          )}
        </div>
        <input id="profilePicture" type="file" accept="image/*" onChange={handleImageChange} />
        <small>Max size 2MB. Supported: PNG, JPG, GIF.</small>
      </section>

      <section className="bio-section">
        <label htmlFor="bio">Profile bio</label>
        <textarea
          id="bio"
          value={bio}
          onChange={(e) => setBio(e.target.value)}
          placeholder="Tell other users a bit about yourself..."
          maxLength={MAX_BIO_LENGTH}
          rows={6}
        />
        <div className="bio-meta">{bio.length}/{MAX_BIO_LENGTH}</div>
      </section>

      <section className="theme-section">
        <label>Theme</label>
        <div className="theme-options">
          <label className={`theme-option ${theme === "light" ? "active" : ""}`}>
            <input type="radio" name="theme" value="light" checked={theme === "light"} onChange={() => setTheme("light")} />
            Light
          </label>
          <label className={`theme-option ${theme === "dark" ? "active" : ""}`}>
            <input type="radio" name="theme" value="dark" checked={theme === "dark"} onChange={() => setTheme("dark")} />
            Dark
          </label>
          <label className={`theme-option ${theme === "colorful" ? "active" : ""}`}>
            <input type="radio" name="theme" value="colorful" checked={theme === "colorful"} onChange={() => setTheme("colorful")} />
            Colorful
          </label>
        </div>
      </section>

      <div className="actions">
        <button onClick={handleSave} className="save">Save</button>
        <button onClick={handleReset} className="reset">Reset</button>
      </div>

      {message && <div className="message">{message}</div>}

      {showConfirm && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Reset preferences?</h3>
            <p>This will restore your profile preferences to their default values. This action cannot be undone.</p>
            <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
              <button onClick={cancelReset} style={{ padding: '0.5rem 0.8rem' }}>Cancel</button>
              <button onClick={confirmReset} style={{ padding: '0.5rem 0.8rem', background: '#d32f2f', color: '#fff', border: 'none', borderRadius: 6 }}>Reset</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CustomizeProfile;
