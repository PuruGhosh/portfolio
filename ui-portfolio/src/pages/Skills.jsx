import React from "react";
import SkillCard from "../component/SkillCard";

export default function Skills() {
  const skills = [
    { name: "React", level: "Advanced" },
    { name: "Java", level: "Intermediate" },
    { name: "Kafka", level: "Intermediate" },
  ];
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
      {skills.map((skill, index) => (
        <SkillCard key={index} {...skill} />
      ))}
    </div>
  );
}