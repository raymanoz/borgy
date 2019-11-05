export interface Intensity {
    number: number;
    label: string;
}

export interface Scale {
    name: string;
    description: string;
    intensities: Intensity[];
}

export type Scales = Scale[];
export type Trials = string[];
