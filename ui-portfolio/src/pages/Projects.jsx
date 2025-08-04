import React from "react";
import ProjectCard from "../component/ProjectCard";

export default function Projects() {
    const projects = [
        {
            title: "Issue Tracker",
            description: "A Jira-like project management tool.",
            tech: ["React", "Spring Boot", "Kafka"],
        },
        {
            title: "Portfolio Website",
            description: "Responsive portfolio built with React.",
            tech: ["React", "Tailwind CSS"],
        },
    ];

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {projects.map((project, index) => (
                <ProjectCard key={index} {...project} />
            ))}
        </div>
    );
}