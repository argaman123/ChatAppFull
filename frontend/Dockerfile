FROM node:16
WORKDIR /frontend
COPY package.json .
RUN npm install
COPY . .
EXPOSE 4200
CMD npm start
