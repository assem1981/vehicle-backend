# Vehicle Backend Deployment Guide

## Quick Start with Docker Compose

### Prerequisites
- Docker and Docker Compose installed
- At least 2GB RAM available

### 1. Configure Environment Variables

Copy the example environment file:
```bash
cp .env.example .env
```

Edit `.env` with your actual values:
```env
MYSQL_ROOT_PASSWORD=your_secure_mysql_password
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
APPLE_CLIENT_ID=your_apple_service_id
APPLE_CLIENT_SECRET=your_apple_client_secret
DEFAULT_ADMIN_EMAIL=admin@vehicle.com
DEFAULT_ADMIN_PASSWORD=YourSecurePassword123!
```

### 2. Build and Start Services

```bash
docker-compose up -d --build
```

### 3. View Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f app
docker-compose logs -f mysql
```

### 4. Check Status

```bash
docker-compose ps
```

### 5. Stop Services

```bash
docker-compose down
```

## Ubuntu Deployment with Nginx

### 1. System Preparation

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo apt install docker-compose -y

# Add your user to docker group
sudo usermod -aG docker $USER
# Logout and login after this
```

### 2. Install Nginx

```bash
sudo apt install nginx -y
sudo systemctl enable nginx
sudo systemctl start nginx
```

### 3. Configure Nginx

Copy the provided nginx configuration:
```bash
sudo cp nginx.conf /etc/nginx/sites-available/vehicle-backend
sudo ln -s /etc/nginx/sites-available/vehicle-backend /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 4. Deploy Application

```bash
# Copy project files to server
cd /opt
sudo mkdir vehicle-backend
cd vehicle-backend

# Upload your files (using scp, git clone, etc.)

# Build and start
docker-compose up -d --build
```

### 5. Configure Firewall

```bash
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 22/tcp
sudo ufw enable
```

## Troubleshooting

### MySQL Connection Error: "Connections using insecure transport are prohibited"

This error occurs when MySQL 8.0 has `require_secure_transport=ON` by default. The docker-compose.yml is configured to disable this requirement for local Docker networks using:

```yaml
command: --default-authentication-plugin=mysql_native_password --require_secure_transport=OFF
```

If you still encounter this error:

1. Ensure MySQL is fully started before the app:
   ```bash
   docker-compose logs mysql
   ```

2. Check health checks are passing:
   ```bash
   docker-compose ps
   ```

3. Verify the connection string in application.yml has `useSSL=false`

### Database Not Ready

The application uses health checks to ensure MySQL, Redis, and ActiveMQ are ready before starting. If you see connection errors:

```bash
# Check service health
docker-compose ps

# Wait for services to be healthy
docker-compose up -d
```

### View Application Logs

```bash
docker-compose logs -f app
```

### Restart Services

```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart app
```

### Reset Everything (WARNING: Deletes Data)

```bash
docker-compose down -v
docker-compose up -d --build
```

## SSL/HTTPS Setup (Optional)

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal is configured automatically
sudo certbot renew --dry-run
```

## Production Considerations

1. **Change Default Passwords**: Use strong passwords in `.env`
2. **Database Backups**: Set up automated MySQL backups
3. **Monitoring**: Implement monitoring and alerting
4. **Secrets Management**: Use proper secrets management (Docker secrets, Vault)
5. **Resource Limits**: Add resource limits to docker-compose.yml
6. **Enable HTTPS**: Use SSL/TLS in production
7. **Security**: Restrict firewall rules, use security groups in cloud

## Service URLs After Deployment

- **Application**: http://localhost:8080 (or http://your-server-ip:8080)
- **Nginx Proxy**: http://localhost (or http://your-domain)
- **ActiveMQ Console**: http://localhost:8161 (admin/admin)
- **MySQL**: localhost:3306 (root/password from .env)
- **Redis**: localhost:6379
