# ${PROJECT_NAME}

A simple React + Node.js web application with search functionality.

**Version:** ${PROJECT_VERSION}  
**Author:** ${AUTHOR_NAME} <${AUTHOR_EMAIL}>

## Features

- React frontend with Vite
- Node.js/Express backend API
- SQLite database (no setup required)
- Search functionality
- No authentication required

## Prerequisites

- [Node.js](https://nodejs.org/) (18+)
- npm or yarn

## Project Structure

```
${PROJECT_NAME}/
├── client/                 # React frontend
│   ├── src/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   └── main.jsx
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── server/                 # Node.js backend
│   ├── src/
│   │   ├── index.js
│   │   ├── database.js
│   │   └── routes/
│   │       └── items.js
│   └── package.json
├── package.json            # Root package.json
├── README.md
└── LICENSE
```

## Installation

```bash
# Install all dependencies (root, client, and server)
npm install
```

## Development

```bash
# Start both client and server in development mode
npm run dev

# Or start them separately:
npm run dev:client    # React app on http://localhost:5173
npm run dev:server    # API server on http://localhost:3000
```

## Production Build

```bash
# Build the client
npm run build

# Start production server
npm start
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/items` | Get all items |
| GET | `/api/items?search=query` | Search items |
| GET | `/api/items/:id` | Get item by ID |
| POST | `/api/items` | Create new item |
| PUT | `/api/items/:id` | Update item |
| DELETE | `/api/items/:id` | Delete item |

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
