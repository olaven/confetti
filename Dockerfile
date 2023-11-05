FROM theasp/clojurescript-nodejs:shadow-cljs-alpine
WORKDIR /app
COPY . /app
RUN npm i
RUN npm run build
CMD npm run start
