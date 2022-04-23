const host = localStorage.getItem('host') || 'http://localhost'
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
