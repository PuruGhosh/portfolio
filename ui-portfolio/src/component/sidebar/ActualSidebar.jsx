import React, {useState} from "react";
import { HomeIcon, FolderIcon, UserIcon, AcademicCapIcon } from "@heroicons/react/24/outline";
import {FaceSmileIcon, ChevronDoubleLeftIcon, ChevronRightIcon} from "@heroicons/react/24/solid";
import {NavLink} from "react-router-dom";

const tabs = [
    { label: "Home", to: "/home", icon: <HomeIcon className="w-7 h-7 mr-2" /> },
    { label: "Skills", to: "/skills", icon: <AcademicCapIcon className="w-7 h-7 mr-2" /> },
    { label: "Projects", to: "/projects", icon: <FolderIcon className="w-7 h-7 mr-2" /> },
    { label: "About Me", to: "/about", icon: <UserIcon className="w-7 h-7 mr-2" /> },
];

export default function ActualSidebar() {
    const [collapsed, setCollapsed] = useState(false);

    return (
        <aside className={`transition-all duration-300 bg-white shadow-lg border-r flex flex-col justify-between relative ${collapsed ? 'w-25 p-4' : 'w-64 p-6'}`}>
            <div>
                <div className="flex items-center justify-between mb-6">
                    <div className="flex items-center gap-3">
                        <FaceSmileIcon className="rounded-full w-10 h-10"/>
                        {!collapsed && <h1 className="text-xl font-bold whitespace-nowrap">My Portfolio</h1>}
                    </div>
                </div>
                <nav className="space-y-2">
                    {tabs.map(({ label, to, icon }) => (
                        <NavLink
                            key={to}
                            to={to}
                            className={({ isActive }) =>
                                `flex items-center gap-2 w-full text-left px-3 py-2 rounded-lg transition text-gray-700 hover:bg-blue-100 hover:text-blue-700 ${
                                    isActive ? "bg-blue-100 text-blue-700 font-medium" : ""
                                }`
                            }
                        >
                            {icon}
                            {!collapsed && <span>{label}</span>}
                        </NavLink>
                    ))}
                </nav>
            </div>
            <button
                onClick={() => setCollapsed(!collapsed)}
                className="absolute bottom-4 right-4 text-gray-600"
            >
                {collapsed ? <ChevronRightIcon className="w-6 h-6" /> : <ChevronDoubleLeftIcon className="w-6 h-6" />}
            </button>
        </aside>
    );
}