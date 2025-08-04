import withMT from "@material-tailwind/html/utils/withMT";
import react from '@vitejs/plugin-react'
import tailwindcss from "@tailwindcss/vite";

export default withMT({
    content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
    esbuild:{
        loader: "jsx",
        include: [
            // Add this for business-as-usual behaviour for .jsx and .tsx files
            "src/**/*.jsx",
            "src/**/*.tsx",
            "node_modules/**/*.jsx",
            "node_modules/**/*.tsx"
        ]
    },
    theme: {
        extend: {},
    },
    plugins: [react(), tailwindcss()],
});


