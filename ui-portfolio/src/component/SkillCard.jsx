import React from "react";

export default function SkillCard({ name, level }) {
    return (
        <div className="border rounded-xl p-4 bg-white shadow">
            <h3 className="text-lg font-semibold text-gray-700">{name}</h3>
            <p className="text-sm text-gray-500">{level}</p>
        </div>
    );
}