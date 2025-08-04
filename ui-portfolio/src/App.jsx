import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import ActualSidebar from "./component/Sidebar/ActualSidebar.jsx";
import Home from "./pages/Home";
import Skills from "./pages/Skills";
import Projects from "./pages/Projects";
import About from "./pages/About";

export default function App() {
    return (
        <Router>
            <div className="flex min-h-screen bg-gray-100">
                <ActualSidebar />
                <main className="flex-1 p-6 overflow-y-auto">
                    <Routes>
                        <Route path="/" element={<Navigate to="/home" />} />
                        <Route path="/home" element={<Home />} />
                        <Route path="/skills" element={<Skills />} />
                        <Route path="/projects" element={<Projects />} />
                        <Route path="/about" element={<About />} />
                    </Routes>
                </main>
            </div>
        </Router>
    );
}