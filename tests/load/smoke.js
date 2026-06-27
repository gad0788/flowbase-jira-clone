import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: __ENV.K6_VUS ? parseInt(__ENV.K6_VUS) : 50,
    duration: __ENV.K6_DURATION || '2m',
    thresholds: {
        http_req_duration: ['p(99)<500'],
        http_req_failed: ['rate<0.01'],
    },
};

const BASE_URL = __ENV.K6_TARGET_URL || 'http://localhost:8080';

export default function () {
    const responses = http.batch([
        ['GET', `${BASE_URL}/actuator/health`, null, { tags: { name: 'health' } }],
        ['GET', `${BASE_URL}/api/v1/ping`, null, { tags: { name: 'ping' } }],
        ['GET', `${BASE_URL}/api/v1/tasks`, null, { tags: { name: 'tasks' } }],
    ]);

    responses.forEach((res) => {
        check(res, {
            'status is 200': (r) => r.status === 200,
            'response time < 500ms': (r) => r.timings.duration < 500,
        });
    });

    sleep(1);
}
