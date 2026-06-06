import React from 'react';

const Footer = () => {
  return (
    <footer className="footer">
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <p>&copy; {new Date().getFullYear()} <span className="gradient-text" style={{ fontWeight: 600 }}>BlogSpace</span>. Built with React &amp; Spring Boot.</p>
        <p style={{ fontSize: '0.75rem', marginTop: '6px', color: 'var(--text-muted)' }}>
          All rights reserved. Gourav vashistha.
        </p>
      </div>
    </footer>
  );
};

export default Footer;
