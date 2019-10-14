import {History} from "history";
import React, {Component} from "react";
import Button from "react-bootstrap/Button";
import {NavLink} from "react-router-dom";
import {JsonDecoder} from "ts.data.json";
import {server} from "../utils/server";
import "./Home.css";
import {Scale, Scales, Trials} from "./Scale";

interface StartTrialResponse {
    url: string;
}

const responseDecoder = JsonDecoder.object<StartTrialResponse>(
    {
        url: JsonDecoder.string,
    },
    "StartTrialResponse",
);

export class Home extends Component<{ history: History }, { scales: Scales, trials: Trials, trialName: string, trialScale: string }> {

    private static renderScaleRadio(scale: Scale) {
        const scaleName = `${scale.name}`;
        const radioId = `scale-${scale.name}`;
        return <option key={radioId} value={scaleName}>{scaleName}</option>;
    }
    constructor(props: Readonly<{ history: History }>) {
        super(props);
        this.state = {
            scales: [],
            trials: [],
            trialName: "",
            trialScale: "",
        };
        // Home.renderScaleRadio = Home.renderScaleRadio.bind(this);
        this.renderActiveTrial = this.renderActiveTrial.bind(this);
        this.startTrialJsonFromState = this.startTrialJsonFromState.bind(this);
        this.startTrial = this.startTrial.bind(this);
        this.completeTrial = this.completeTrial.bind(this);
    }

    public componentDidMount() {
        fetch(server.scales)
            .then((result) => result.json())
            .then((s) => this.setState({scales: s}));

        this.refreshTrials();
    }

    public render() {
        return <div className="container">
            <div className="row col justify-content-center"><h1>Borgy</h1></div>
            <span id="newTrial">
                <div className="row col"><h2>New trial</h2></div>
                <div className="row col">
                    <div className="input-group mb-3">
                      <div className="input-group-prepend">
                        <span className="input-group-text" id="basic-addon1">Name</span>
                      </div>
                      <input type="text" className="form-control" placeholder="Name" aria-label="Name"
                             aria-describedby="basic-addon1" onChange={(event) => this.onTrialNameChanged(event.target.value)}/>
                    </div>

                    <div className="input-group mb-3">
                        <div className="input-group-prepend">
                            <label className="input-group-text" htmlFor="scale">Scale</label>
                        </div>
                        <select className="custom-select" id="scale" defaultValue="choose"
                                onChange={(event) => this.onTrialScaleChanged(event.target.value)}>
                            <option value="choose">Choose...</option>
                            {this.state ? this.state.scales.map(Home.renderScaleRadio) : <span/>}
                        </select>
                    </div>
                </div>
                <Button onClick={this.startTrial}>Begin</Button>
            </span>

            <span id="activeTrials">
                <div className="row col"><h2>Active Trials</h2></div>
                {this.state ? this.state.trials.map(this.renderActiveTrial) : <span>Loading active trials</span>}
            </span>
        </div>;
    }

    private refreshTrials() {
        fetch(server.trials)
            .then((result) => result.json())
            .then((t) => this.setState({trials: t}));
    }

    private startTrial() {
        fetch(server.trials, {method: "POST", body: this.startTrialJsonFromState()})
        // TODO : Handle failure response
            .then((result) => result.json())
            .then((json) => responseDecoder
                .decodePromise(json)
                .then((value) => this.props.history.push(value.url)),
            );

    }

    private startTrialJsonFromState() {
        return `{ "name": "${this.state.trialName}", "scale": "${this.state.trialScale}" }`;
    }

    private onTrialNameChanged(value: string) {
        this.setState({trialName: value});
    }

    private onTrialScaleChanged(value: string) {
        this.setState({trialScale: value});
    }

    private renderActiveTrial(trial: string, idx: number) {
        return <div key={idx} className="row active-trial">
            <div className="col-4"><NavLink to={"/trial/" + trial}>{trial}</NavLink></div>
            <button className="btn btn-danger" onClick={(_) => this.completeTrial(trial)}>Complete</button>
        </div>;
    }

    private completeTrial(trialName: string) {
        fetch(server.trial(trialName), {method: "DELETE", credentials: "same-origin"})
            .then((_) => this.refreshTrials());
    }

}
