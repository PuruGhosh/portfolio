import React from "react";

export default function ProjectCard({ title, description, tech }) {
    return (
        <div className="border border-gray-300 rounded-xl p-4 shadow-sm bg-white">
            <h2 className="text-xl font-semibold text-gray-800">{title}</h2>
            <p className="text-sm text-gray-600 mt-1">{description}</p>
            <div className="mt-2 flex flex-wrap gap-2">
                {tech.map((item, index) => (
                    <span
                        key={index}
                        className="bg-gray-200 text-xs px-2 py-1 rounded-full"
                    >
            {item}
          </span>
                ))}
            </div>
        </div>
    );
}
