export interface Server {
    scales: string;
    scale(name: string): string
    trials: string;
    trial(name: string): string
}

let reactappserver = process.env.REACT_APP_SERVER;
export let server: Server = {
    scales: `${reactappserver}/api/scales`,
    scale(name: string): string {return `${this.scales}/${name}`;},
    trials: `${reactappserver}/api/trials`,
    trial(name: string): string {return `${this.trials}/${name}`;}
};


