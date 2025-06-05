export interface Policy {
    id: number;
    name: string;
    description: string;
    premiumAmount: number;
    coverageAmount: number;
    durationMonths: number;
    renewalPremiumRate: number;
    createdAt?: string | null;
  }
  
  export interface User {
    id: number;
    name: string;
    email: string;
    phone: string;
    address: string;
    role: string;
  }
  
  export interface PurchasedPolicy {
    id: number;
    user: User;
    policy: Policy;
    startDate: string;
    endDate: string;
    status: string;
    premiumPaid: number;
  }