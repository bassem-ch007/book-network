Vagrant.configure("2") do |config|
  # --- Base image ---
      config.vm.box = "ubuntu/jammy64"
  config.vm.hostname = "bsn-prod"

  # --- Networking ---
  config.vm.network "forwarded_port", guest: 22, host: 2222      # SSH
  config.vm.network "forwarded_port", guest: 80, host: 8080    # proxy
  config.vm.network "forwarded_port", guest: 8088, host: 8088  # backend
  config.vm.network "forwarded_port", guest: 4200, host: 4200  # frontend
  config.vm.network "forwarded_port", guest: 5432, host: 5432  # postgres

  # --- Shared folder ---
  config.vm.synced_folder ".", "/vagrant", type: "virtualbox"

  # --- VM Resources ---
  config.vm.provider "virtualbox" do |vb|
    vb.name = "bsn-prod"
    vb.memory = 4096
    vb.cpus = 2
  end

  # --- Provisioning ---
  config.vm.provision "shell", inline: <<-SHELL
    set -e

    echo "=============================="
    echo " Loading environment variables"
    echo "=============================="
    if [ -f /vagrant/.env ]; then
      export $(grep -v '^#' /vagrant/.env | xargs)
    else
      echo ".env file missing! Exiting."
      exit 1
    fi

    echo "=============================="
    echo " Updating packages"
    echo "=============================="
    apt-get update -y
    apt-get install -y ca-certificates curl gnupg lsb-release unzip

    echo "=============================="
    echo " Installing Docker (non-interactive)"
    echo "=============================="
    mkdir -p /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor > /etc/apt/keyrings/docker.gpg
    echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
      https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" \
      > /etc/apt/sources.list.d/docker.list

    apt-get update -y
    apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
    usermod -aG docker vagrant
    systemctl enable docker
    systemctl start docker
    echo " Docker installed successfully"

    echo "=============================="
    echo " Installing Docker Compose binary (latest)"
    echo "=============================="
    curl -L "https://github.com/docker/compose/releases/download/v2.27.0/docker-compose-$(uname -s)-$(uname -m)" \
      -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    echo "Docker Compose installed"


    echo "=============================="
    echo " Logging into Docker Hub"
    echo "=============================="
    echo "$DOCKERHUB_PASSWORD" | docker login --username "$DOCKERHUB_USERNAME" --password-stdin || true

    echo "=============================="
    echo "Pulling Docker images"
    echo "=============================="
    docker pull bassem00/bsn-backend:1.0.3 || true
    docker pull bassem00/bsn-frontend:1.0.1 || true
    docker pull bassem00/bsn-proxy:1.0.0 || true
    docker pull postgres || true

    echo "=============================="
    echo " Creating Postgres init schema script"
    echo "=============================="
    cat <<'SQL' > /home/vagrant/init-schema.sql
CREATE SCHEMA IF NOT EXISTS my_schema;
SQL

    echo "=============================="
    echo " Creating docker-compose.yml"
    echo "=============================="
    cat <<'EOF' > /home/vagrant/docker-compose.yml
services:
  postgres:
    image: postgres:15
    container_name: postgres-sql-bsn
    environment:
      POSTGRES_USER: username
      POSTGRES_PASSWORD: password
      POSTGRES_DB: book_social_network
      PGDATA: /var/lib/postgresql/data
    volumes:
      - bsn_postgres:/var/lib/postgresql/data
      - /home/vagrant/init-schema.sql:/docker-entrypoint-initdb.d/init-schema.sql:ro
    ports:
      - "5432:5432"
    networks:
      - spring-demo
    restart: unless-stopped

  backend:
    image: bassem00/bsn-backend:1.0.3
    container_name: bsn-api
    ports:
      - "8088:8088"
    depends_on:
      - postgres
    networks:
      - spring-demo

  frontend:
    image: bassem00/bsn-frontend:1.0.1
    container_name: bsn-ui
    ports:
      - "4200:80"
    depends_on:
      - backend
    networks:
      - spring-demo

  proxy:
    image: bassem00/bsn-proxy:1.0.0
    container_name: bsn-proxy
    ports:
      - "80:80"
    depends_on:
      - backend
      - frontend
    networks:
      - spring-demo

volumes:
  bsn_postgres:
    driver: local

networks:
  spring-demo:
    driver: bridge
EOF

    echo "=============================="
    echo " Starting containers"
    echo "=============================="
    docker-compose -f /home/vagrant/docker-compose.yml up -d

    echo "=============================="
    echo " Installing Ngrok (latest v3)"
    echo "=============================="
    # Remove any old ngrok
    sudo rm -f /usr/local/bin/ngrok
    sudo rm -rf /home/vagrant/.ngrok2 /home/vagrant/.config/ngrok

    # Download latest ngrok v3
    NGROK_VERSION="3.30.0"  # specify the version
    curl -sSL "https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v${NGROK_VERSION}-linux-amd64.tgz" -o /tmp/ngrok.tgz

    # Extract and move to /usr/local/bin
    sudo tar -xzf /tmp/ngrok.tgz -C /usr/local/bin
    sudo chmod +x /usr/local/bin/ngrok
    rm /tmp/ngrok.tgz

    # Verify installation
    ngrok version || echo "Ngrok installation failed"

    echo "=============================="
    echo " Adding Ngrok authtoken"
    echo "=============================="
    # Make v3 config directory
    su - vagrant -c "mkdir -p /home/vagrant/.config/ngrok"

    TOKEN=$(echo $NGROK_AUTH_TOKEN | tr -d '\r' | tr -d '"')
    su - vagrant -c "ngrok config add-authtoken $TOKEN"

    echo "=============================="
    echo " Launching Ngrok tunnel"
    echo "=============================="
    # Run the tunnel in the background and log output
    su - vagrant -c "nohup ngrok http 80 --log=stdout > /home/vagrant/ngrok.log 2>&1 &"

    echo "Ngrok v3 started Use 'vagrant ssh' and check the tunnel URL with 'cat /home/vagrant/ngrok.log'."
  SHELL
end
