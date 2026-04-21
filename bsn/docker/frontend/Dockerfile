# ---- Serve with NGINX ----
FROM nginx:alpine
# Copy custom nginx config (optional, for routing)
COPY nginx.conf /etc/nginx/nginx.conf
# Copy built Angular files to NGINX html folder
COPY dist/bsn-ui /usr/share/nginx/html
# Expose port 80
EXPOSE 80
