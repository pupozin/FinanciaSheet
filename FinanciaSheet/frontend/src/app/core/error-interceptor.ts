import { HttpInterceptorFn } from '@angular/common/http';
export const errorInterceptor: HttpInterceptorFn = (req, next) => next(req);
