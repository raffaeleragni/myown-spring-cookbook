
const socket = new SockJS('/sock');
const client = Stomp.over(socket);

client.connect({}, frame => {
  console.log('Connected: ' + frame);
  client.subscribe('/response', event => {
    console.log('Event from server: ', event);
  });
  client.send("/message", {}, JSON.stringify("message"));
});
