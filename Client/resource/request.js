const host = localStorage.getItem('host') || 'http://192.168.3.2:8080'
const instance = axios.create({
    baseURL: host,
    timeout: 10000
})

function sendGet(path, handle) {
    instance.get(path).then(res => {
        if (res.status == 200) {
            if (res.data.result) {
                handle(res.data.data)
            }
        }
    })
}
