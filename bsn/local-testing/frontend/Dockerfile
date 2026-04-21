# ---- Stage 1: Build Angular app ----
FROM node:22-alpine AS build
# Set working directory
WORKDIR /app
# Copy package.json and package-lock.json (for caching)
COPY package*.json ./
# Install dependencies
RUN npm ci
# Copy the rest of the source code
COPY . .
# Build the Angular app in production mode
RUN npm run build -- --configuration production

# ---- Stage 2: Serve with NGINX ----
FROM nginx:alpine
# Copy custom nginx config (optional, for routing)
COPY nginx.conf /etc/nginx/nginx.conf
# Copy built Angular files to NGINX html folder
COPY --from=build /app/dist/bsn-ui /usr/share/nginx/html
# Expose port 80
EXPOSE 80
