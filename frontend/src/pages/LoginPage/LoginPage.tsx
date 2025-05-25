// src/pages/LoginPage/LoginPage.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { authApi } from "../../api/authApi";
import ErrorMessage from "../../components/ErrorMessage/ErrorMessage";
import styles from "./Login.Page.module.scss";
import { useAuth } from "../../context/useAuth";

const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [error, setError] = useState<string>("");

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    try {
      const response = await authApi.login({ email, password });
      login(response.data.token);
      navigate("/transactions");
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (error: any) {
      if (error.response && error.response.data) {
        setError(error.response.data); // wiadomość z backendu
      } else {
        setError("Wystąpił nieznany błąd podczas logowania.");
      }
    }
  };

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>Logowanie</h2>
      <form onSubmit={handleLogin} className={styles.form}>
        <div className={styles.formGroup}>
          <label>Email:</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            className={styles.input}
          />
          {error && <ErrorMessage message={error} />}
        </div>
        <div className={styles.formGroup}>
          <label>Hasło:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            className={styles.input}
          />
        </div>
        <button type="submit" className={styles.button}>
          Zaloguj
        </button>
      </form>
    </div>
  );
};

export default LoginPage;
